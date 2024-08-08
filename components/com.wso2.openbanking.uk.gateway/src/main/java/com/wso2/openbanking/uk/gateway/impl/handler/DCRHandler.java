package com.wso2.openbanking.uk.gateway.impl.handler;

import com.wso2.openbanking.uk.common.constants.HttpHeader;
import com.wso2.openbanking.uk.common.constants.HttpHeaderContentType;
import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.util.HttpUtil;
import com.wso2.openbanking.uk.common.util.StringUtil;
import com.wso2.openbanking.uk.gateway.constants.GatewayConstants;
import com.wso2.openbanking.uk.gateway.core.JWTValidator;
import com.wso2.openbanking.uk.gateway.core.OBClientRegistrationResponse1;
import com.wso2.openbanking.uk.gateway.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.exception.JWTValidatorRuntimeException;
import com.wso2.openbanking.uk.gateway.exception.OpenBankingAPIHandlerException;
import com.wso2.openbanking.uk.gateway.util.ServiceProviderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * This class is the handler for the Dynamic Client Registration (DCR) API.
 */
public class DCRHandler extends OpenBankingAPIHandler {
    private static final Log log = LogFactory.getLog(DCRHandler.class);

    private static final String IS_USERNAME = "admin";
    private static final String IS_PASSWORD = "admin";

    public DCRHandler() {
        super();

        String amHost = GatewayConstants.DEFAULT_AM_HOST;
    }

    @Override
    protected boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
        return msgInfoDTO.getResource().toLowerCase(Locale.getDefault()).contains("/register");
    }

    @Override
    protected ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();

        // If the request is a POST, or PUT request, then the request payload should be validated.
        HttpMethod httpMethod = HttpMethod.valueOf(requestContextDTO.getMsgInfo().getHttpMethod());

        if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
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
        HttpMethod httpMethod = HttpMethod.valueOf(requestContextDTO.getMsgInfo().getHttpMethod());

        // If the request is a GET, PUT, or DELETE request, then the clientId sent in the path should be verified.
        if (
                httpMethod.equals(HttpMethod.GET) ||
                        httpMethod.equals(HttpMethod.PUT) ||
                        httpMethod.equals(HttpMethod.DELETE)
        ) {
            String clientIdSentInRequest = HttpUtil.extractPathVariableSentAsLastSegment(
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

        String username = IS_USERNAME;
        String password = IS_PASSWORD;

        // Extract the payload and the headers from the request context
        String modifiedPayload = getPayload(requestContextDTO.getMsgInfo());
        Map<String, String> headers = requestContextDTO.getMsgInfo().getHeaders();

        if (
                httpMethod.equals(HttpMethod.POST) ||
                        httpMethod.equals(HttpMethod.PUT)
        ) {
            if (modifiedPayload == null) {
                log.error("Request payload is null");
                throw new OpenBankingAPIHandlerException("Request payload is null");
            }

            // Set the modified payload to the request context
            modifiedPayload = ServiceProviderUtil
                    .convertOBClientRegistrationRequest1JsonStringToISDCRPayload(modifiedPayload);

            if (modifiedPayload == null) {
                log.error("Error occurred while mapping the request payload to the IS DCR API request");
                throw new OpenBankingAPIHandlerException(
                        "Error occurred while mapping the request payload to the IS DCR API request"
                );
            }

            // Set the Content-Type header to application/json
            headers.replace(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);

            headers.put("WSO2-Identity-User", username);
        }

        // Generate the Basic Auth header
        String basicAuthHeader = HttpUtil.generateBasicAuthHeader(username, password);

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
    protected ExtensionResponseDTO preProcessResponse(
            ResponseContextDTO responseContextDTO
    ) throws OpenBankingAPIHandlerException {
        if (responseContextDTO.getStatusCode() < 200 || responseContextDTO.getStatusCode() >= 300) {
            String error = String.format(
                    "Backend responded with an error: %d %s",
                    responseContextDTO.getStatusCode(),
                    getPayload(responseContextDTO.getMsgInfo())
            );
            // log.error(StringUtil.sanitizeString(error));
            throw new OpenBankingAPIHandlerException(StringUtil.sanitizeString(error));
        }

        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();

        // Convert the payload to the Open Banking compliant response
        switch (HttpMethod.valueOf(responseContextDTO.getMsgInfo().getHttpMethod())) {
            case GET:
            case POST:
            case PUT:
                String payload = getPayload(responseContextDTO.getMsgInfo());
                extensionResponseDTO.setHeaders(responseContextDTO.getMsgInfo().getHeaders());
                OBClientRegistrationResponse1 obClientRegistrationResponse1 =
                        new OBClientRegistrationResponse1(payload);
                String modifiedPayload = obClientRegistrationResponse1.getOBClientRegistrationResponse1();
                extensionResponseDTO.setPayload(
                        new ByteArrayInputStream(
                                modifiedPayload
                                        .getBytes(StandardCharsets.UTF_8)
                        )
                );
                break;
            case DELETE:
                break;
            default:
                String error = String.format("Unsupported HTTP method: %s",
                        StringUtil.sanitizeString(responseContextDTO.getMsgInfo().getHttpMethod())
                );
                // log.error(StringUtil.sanitizeString(error));
                throw new OpenBankingAPIHandlerException(
                        "Unsupported HTTP method: " + responseContextDTO.getMsgInfo().getHttpMethod()
                );
        }

        return extensionResponseDTO;
    }
}
