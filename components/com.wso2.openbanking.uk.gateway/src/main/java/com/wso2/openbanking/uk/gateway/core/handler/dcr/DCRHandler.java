package com.wso2.openbanking.uk.gateway.core.handler.dcr;

import com.wso2.openbanking.uk.gateway.common.util.StringUtil;
import com.wso2.openbanking.uk.gateway.core.handler.dcr.isserviceprovider.ISServiceProvider;
import com.wso2.openbanking.uk.gateway.handler.constants.HttpHeader;
import com.wso2.openbanking.uk.gateway.handler.constants.HttpHeaderContentType;
import com.wso2.openbanking.uk.gateway.handler.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.handler.exception.OpenBankingAPIHandlerException;
import com.wso2.openbanking.uk.gateway.core.handler.dcr.jwt.JWTValidator;
import com.wso2.openbanking.uk.gateway.core.handler.dcr.jwt.JWTValidatorRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.common.gateway.dto.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * This class is the handler for the Dynamic Client Registration (DCR) API.
 */
public class DCRHandler extends OpenBankingAPIHandler {
    private static final Log log = LogFactory.getLog(DCRHandler.class);

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    // TODO : This is a temporary solution. Need to be removed once the proper authentication mechanism is implemented.
    private static final String IS_USERNAME = "admin";
    private static final String IS_PASSWORD = "admin";

    @Override
    protected boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
        return msgInfoDTO.getResource().toLowerCase(Locale.getDefault()).contains("/register");
    }

    @Override
    protected ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();

        // If the request is a POST, or PUT request, then the request payload should be validated.
        String httpMethod = requestContextDTO.getMsgInfo().getHttpMethod();

        if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
            // Extract the request payload
            String requestPayload = getPayload(requestContextDTO.getMsgInfo());

            if (requestPayload == null) {
                log.error("Request payload is null");
                throw new OpenBankingAPIHandlerException("Request payload is null");
            }

            // Validate the JWT token in the request payload
            JWTValidator requestPayloadValidator = new JWTValidator(requestPayload);
            if (!requestPayloadValidator.validateJwt()) {
                log.error("Invalid JWT token in the request payload");
                throw new OpenBankingAPIHandlerException("Invalid JWT token in the request payload");
            }

            // In order to validate the payload signature, extract the JWKSetEndpoint from the software_statement field
            String softwareStatement = requestPayloadValidator.getClaim("software_statement", String.class);
            if (softwareStatement == null) {
                log.error("software_statement claim not found in the request payload");
                throw new OpenBankingAPIHandlerException("software_statement claim not found in the request payload");
            }

            // Software Statement is a JWT token, so validate it
            JWTValidator softwareStatementValidator = new JWTValidator(softwareStatement);
            if (!softwareStatementValidator.validateJwt()) {
                log.error("Invalid software_statement JWT token in the request payload");
                throw new OpenBankingAPIHandlerException("Invalid software_statement JWT token in the request payload");
            }

            // Extract the JWKSetEndpoint from the software_statement field
            String jwkSetEndpoint = softwareStatementValidator.getClaim("software_jwks_endpoint", String.class);
            if (jwkSetEndpoint == null) {
                log.error("software_jwks_endpoint claim not found in the software_statement JWT token");
                throw new OpenBankingAPIHandlerException(
                        "software_jwks_endpoint claim not found in the software_statement JWT token");
            }

            // Validate the request payload against the JWKSetEndpoint
            boolean isValidSignature = false;
            try {
                isValidSignature = requestPayloadValidator.validateSignatureUsingJWKS(jwkSetEndpoint);
            } catch (JWTValidatorRuntimeException e) {
                log.error("Error occurred while validating the request payload signature", e);
                throw new OpenBankingAPIHandlerException(
                        "Error occurred while validating the request payload signature", e);
            }

            if (!isValidSignature) {
                log.error("Invalid signature in the request payload");
                throw new OpenBankingAPIHandlerException("Invalid signature in the request payload");
            }

            // Set the modified payload to the request context
            String modifiedPayload = requestPayloadValidator.getJSONString();
            extensionResponseDTO.setPayload(new ByteArrayInputStream(modifiedPayload.getBytes(StandardCharsets.UTF_8)));

            // Set the Content-Type header to application/json
            Map<String, String> headers = requestContextDTO.getMsgInfo().getHeaders();
            headers.replace(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);

            extensionResponseDTO.setHeaders(headers);
        }

        return extensionResponseDTO;
    }

    @Override
    protected ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        String httpMethod = requestContextDTO.getMsgInfo().getHttpMethod();

        // If the request is a GET, PUT, or DELETE request, then the clientId sent in the path should be verified.
        if(
                StringUtil.equalsIgnoreCase(httpMethod, HTTP_METHOD_GET) ||
                        StringUtil.equalsIgnoreCase(httpMethod, HTTP_METHOD_PUT)  ||
                        StringUtil.equalsIgnoreCase(httpMethod, HTTP_METHOD_DELETE)
        ) {
            String clientIdSentInRequest = extractPathVariableSentAsLastSegment(
                    requestContextDTO
                            .getMsgInfo()
                            .getResource()
            );

            // Extract the clientId from the request token
            String clientIdBoundToToken = requestContextDTO.getApiRequestInfo().getConsumerKey();

            if (!clientIdSentInRequest.equals(clientIdBoundToToken)) {
                log.error("Client ID in the request path does not match the client ID in the request token");
                throw new OpenBankingAPIHandlerException(
                        "Client ID in the request path does not match the client ID in the request token"
                );
            }
        }

        // Extract the payload and the headers from the request context
        String modifiedPayload = getPayload(requestContextDTO.getMsgInfo());
        Map<String, String> headers = requestContextDTO.getMsgInfo().getHeaders();

        if (
                StringUtil.equalsIgnoreCase(httpMethod, HTTP_METHOD_POST)  ||
                        StringUtil.equalsIgnoreCase(httpMethod, HTTP_METHOD_PUT)
        ) {
            if (modifiedPayload == null) {
                log.error("Request payload is null");
                throw new OpenBankingAPIHandlerException("Request payload is null");
            }

//            // Validate the request payload
//            if (!validateRequestPayload(modifiedPayload)) {
//                log.error("Invalid request payload");
//                throw new OpenBankingAPIHandlerException("Invalid request payload");
//            }

            // Set the modified payload to the request context
            modifiedPayload = ISServiceProvider.convertJsonStringToISDCRRequestJsonString(modifiedPayload);

            if (modifiedPayload == null) {
                log.error("Error occurred while mapping the request payload to the IS DCR API request");
                throw new OpenBankingAPIHandlerException(
                        "Error occurred while mapping the request payload to the IS DCR API request"
                );
            }

            debugPrintJsonString(modifiedPayload);

            // Set the Content-Type header to application/json
            headers.replace(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);

        }

        // TODO : Get the IS admin username and password from APIMConfigurationManager
        String username = IS_USERNAME;
        String password = IS_PASSWORD;

        // Generate the Basic Auth header
        String basicAuthHeader = generateBasicAuthHeader(username, password);

        // Add the Basic Auth header to the request headers
        headers.put(HttpHeader.AUTHORIZATION, basicAuthHeader);

        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();

        // Set the modified headers to the extension response
        extensionResponseDTO.setHeaders(headers);

        // Set the modified payload to the extension response
        if (modifiedPayload != null) {
            extensionResponseDTO.setPayload(new ByteArrayInputStream(modifiedPayload.getBytes(StandardCharsets.UTF_8)));
        }

        return extensionResponseDTO;
    }

    @Override
    protected ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {

        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();

        String payload = getPayload(responseContextDTO.getMsgInfo());
        extensionResponseDTO.setHeaders(responseContextDTO.getMsgInfo().getHeaders());

        if (payload != null) {
            extensionResponseDTO.setPayload(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));
        }

        String httpMethod = responseContextDTO.getMsgInfo().getHttpMethod();

        switch (httpMethod) {
            case HTTP_METHOD_GET:
                processResponseHttpMethodGet(extensionResponseDTO, responseContextDTO);
                break;
            case HTTP_METHOD_POST:
                processResponseHttpMethodPost(extensionResponseDTO, responseContextDTO);
                break;
            case HTTP_METHOD_PUT:
                processResponseHttpMethodPut(extensionResponseDTO, responseContextDTO);
                break;
            case HTTP_METHOD_DELETE:
                processResponseHttpMethodDelete(extensionResponseDTO, responseContextDTO);
                break;
            default:
                String error = String.format("Unsupported HTTP method: %s", StringUtil.sanitizeString(httpMethod));
                log.error(StringUtil.sanitizeString(error));
                throw new OpenBankingAPIHandlerException("Unsupported HTTP method: " + httpMethod);
        }

        return extensionResponseDTO;
    }

    private void processResponseHttpMethodGet(
            ExtensionResponseDTO extensionResponseDTO,
            ResponseContextDTO responseContextDTO
    ) throws OpenBankingAPIHandlerException {
    }

    private void processResponseHttpMethodPost(
            ExtensionResponseDTO extensionResponseDTO,
            ResponseContextDTO responseContextDTO
    ) throws OpenBankingAPIHandlerException {
    }

    private void processResponseHttpMethodPut(
            ExtensionResponseDTO extensionResponseDTO,
            ResponseContextDTO responseContextDTO
    ) throws OpenBankingAPIHandlerException {
    }

    private void processResponseHttpMethodDelete(
            ExtensionResponseDTO extensionResponseDTO,
            ResponseContextDTO responseContextDTO
    ) throws OpenBankingAPIHandlerException {
    }

    private static String extractPathVariableSentAsLastSegment(String resource) {
        // If the resource ends with a "/", then remove it
        if (resource.endsWith("/")) {
            resource = resource.substring(0, resource.length() - 1);
        }

        // Split the resource by "/"
        String[] segments = resource.split("/");

        // Return the last segment
        return segments[segments.length - 1];
    }

    private static String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + new String(
                Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
    }

    private static void debugPrintJsonString(String requestPayload) {
        Function<String, String[]> jsonLineSplitter = jsonString ->  {
            List<String> lines = new ArrayList<>();
            StringBuilder currentLine = new StringBuilder();
            int indentLevel = 0;
            boolean inQuotes = false;

            for (char c : jsonString.toCharArray()) {
                // Handle quotes for string literals in JSON
                if (c == '\"') {
                    inQuotes = !inQuotes;
                }

                // Handle newline characters
                if (c == '\n' || c == '\r') {
                    continue;
                }

                // Handle opening braces and brackets outside of quotes
                if (!inQuotes && (c == '{' || c == '[')) {
                    currentLine.append(c);
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    indentLevel++;
                    currentLine.append(" ".repeat(indentLevel * 2));
                    continue;
                }

                // Handle closing braces and brackets outside of quotes
                if (!inQuotes && (c == '}' || c == ']')) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    indentLevel--;
                    currentLine.append(" ".repeat(indentLevel * 2));
                }

                currentLine.append(c);

                // Handle commas outside of quotes to split lines for objects and arrays
                if (!inQuotes && c == ',') {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    currentLine.append(" ".repeat(indentLevel * 2));
                }
            }

            // Add any remaining content in the buffer to the list
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }

            // Convert the list to an array and return
            return lines.toArray(new String[0]);
        };

        String[] jsonLines = jsonLineSplitter.apply(requestPayload);
        for (String line : jsonLines) {
            log.debug(line.replaceAll("[\r\n]", ""));
        }
    }
}
