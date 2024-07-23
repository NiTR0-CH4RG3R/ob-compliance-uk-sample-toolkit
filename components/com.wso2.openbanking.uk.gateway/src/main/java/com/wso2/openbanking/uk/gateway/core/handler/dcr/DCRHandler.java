package com.wso2.openbanking.uk.gateway.core.handler.dcr;

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
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

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
                httpMethod.toUpperCase(Locale.getDefault()).equals(HTTP_METHOD_GET) ||
                        httpMethod.toUpperCase(Locale.getDefault()).equals(HTTP_METHOD_PUT) ||
                        httpMethod.toUpperCase(Locale.getDefault()).equals(HTTP_METHOD_DELETE)
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

        String modifiedPayload = getPayload(requestContextDTO.getMsgInfo());

        if (
                httpMethod.toUpperCase(Locale.getDefault()).equals(HTTP_METHOD_POST) ||
                        httpMethod.toUpperCase(Locale.getDefault()).equals(HTTP_METHOD_PUT)
        ) {
            // TODO : Map the parameters coming in the request payload to the IS DCR API request parameters
        }

        // TODO : Get the IS admin username and password from APIMConfigurationManager
        String username = IS_USERNAME;
        String password = IS_PASSWORD;

        // Generate the Basic Auth header
        String basicAuthHeader = generateBasicAuthHeader(username, password);

        // Add the Basic Auth header to the request headers
        Map<String, String> headers = requestContextDTO.getMsgInfo().getHeaders();
        headers.put(HttpHeader.AUTHORIZATION, basicAuthHeader);

        // Set the modified headers to the extension response
        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();
        extensionResponseDTO.setHeaders(headers);
        extensionResponseDTO.setPayload(new ByteArrayInputStream(modifiedPayload.getBytes(StandardCharsets.UTF_8)));

        return extensionResponseDTO;
    }

    @Override
    protected ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {
        log.debug("DCRHandler preProcessResponse");
        return null;
    }

    @Override
    protected ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {
        log.debug("DCRHandler postProcessResponse");
        return null;
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
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
