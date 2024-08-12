package com.wso2.openbanking.uk.gateway.impl.handler;

import com.wso2.openbanking.uk.common.constants.HttpHeader;
import com.wso2.openbanking.uk.common.constants.HttpHeaderContentType;
import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.util.HttpUtil;
import com.wso2.openbanking.uk.gateway.constants.GatewayConstants;
import com.wso2.openbanking.uk.gateway.core.JWTValidator;
import com.wso2.openbanking.uk.gateway.core.OBClientRegistrationResponse1;
import com.wso2.openbanking.uk.gateway.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.exception.JWTValidatorRuntimeException;
import com.wso2.openbanking.uk.gateway.util.OpenBankingAPIHandlerUtil;
import com.wso2.openbanking.uk.gateway.util.ServiceProviderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseStatus;
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
public class DCRHandler implements OpenBankingAPIHandler {
    private static final Log log = LogFactory.getLog(DCRHandler.class);

    public DCRHandler() {
    }

    @Override
    public boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
        return msgInfoDTO.getResource().toLowerCase(Locale.getDefault()).contains("/register");
    }

    @Override
    public ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {

        // If the request contains a payload, it should be validated. According to the UK Open Banking DCR spec,
        // only the POST and PUT requests contain a payload.

        // Get the HTTP method of the request.
        HttpMethod httpMethod = HttpMethod.valueOf(requestContextDTO.getMsgInfo().getHttpMethod());

        // Extract the payload from the request context. This returns null if the payload is not present.
        String requestPayload = OpenBankingAPIHandlerUtil.getPayload(requestContextDTO.getMsgInfo());

        if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {

            // If the POST or PUT request does not contain a payload, then it is a malformed request.
            // Return a 400 Bad Request response and stop the request processing.
            if (requestPayload == null) {
                log.error("Request payload is null");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            JWTValidator requestPayloadValidator = new JWTValidator(requestPayload);

            // This method will validate the format of the JWT token in the request payload.
            // If the JWT is malformed or not in the correct format, return false.
            if (!requestPayloadValidator.validateJwt()) {
                log.error("Invalid JWT token in the request payload");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // In order to validate the payload signature, extract the JWKSetEndpoint from the software_statement field.
            String softwareStatement = requestPayloadValidator.getClaim("software_statement", String.class);
            if (softwareStatement == null) {
                log.error("software_statement claim not found in the request payload");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // Software Statement is a JWT token, so validate it
            JWTValidator softwareStatementValidator = new JWTValidator(softwareStatement);
            if (!softwareStatementValidator.validateJwt()) {
                log.error("Invalid software_statement JWT token in the request payload");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // Extract the JWKSetEndpoint from the software_statement field
            String jwkSetEndpoint = softwareStatementValidator.getClaim("software_jwks_endpoint", String.class);
            if (jwkSetEndpoint == null) {
                log.error("software_jwks_endpoint claim not found in the software_statement JWT token");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // Validate the signature of the request payload, and the software statement using the JWKSetEndpoint.

            // Validate the signature of the request payload
            boolean isPayloadSignatureValid = false;
            try {
                isPayloadSignatureValid = requestPayloadValidator.validateSignatureUsingJWKS(jwkSetEndpoint);
            } catch (JWTValidatorRuntimeException e) {
                log.error("Error occurred while validating the request payload signature", e);
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            if (!isPayloadSignatureValid) {
                log.error("Invalid signature in the request payload");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // Validate the signature of the software statement
            boolean isSoftwareStatementSignatureValid = false;
            try {
                isSoftwareStatementSignatureValid = softwareStatementValidator.validateSignatureUsingJWKS(jwkSetEndpoint);
            } catch (JWTValidatorRuntimeException e) {
                log.error("Error occurred while validating the software statement signature", e);
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            if (!isSoftwareStatementSignatureValid) {
                log.error("Invalid signature in the software statement");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // If the execution reaches this point, the request payload and the software statement are valid.

            // Convert the request payload to a JSON string
            String modifiedPayload = requestPayloadValidator.getJSONString();

            // Set the Content-Type header to application/json
            Map<String, String> headers = requestContextDTO.getMsgInfo().getHeaders();
            headers.replace(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);

            return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                    200,
                    modifiedPayload,
                    headers,
                    null,
                    ExtensionResponseStatus.CONTINUE
            );
        } else {
            // If a request other than POST or PUT is received, then the request payload should be null.
            if (requestPayload != null) {
                log.error("Request payload is not null for a GET or DELETE request");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }
        }

        return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                200,
                null,
                requestContextDTO.getMsgInfo().getHeaders(),
                null,
                ExtensionResponseStatus.CONTINUE
        );
    }

    @Override
    public ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
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
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Client ID in the request path does not match the client ID in the request token",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }
        }

        // Extract the payload and the headers from the request context
        String modifiedPayload = OpenBankingAPIHandlerUtil.getPayload(requestContextDTO.getMsgInfo());
        Map<String, String> headers = requestContextDTO.getMsgInfo().getHeaders();

        if (
                httpMethod.equals(HttpMethod.POST) ||
                httpMethod.equals(HttpMethod.PUT)
        ) {
            if (modifiedPayload == null) {
                log.error("Request payload is null");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        400,
                        "Malformed request payload",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // Set the modified payload to the request context
            modifiedPayload = ServiceProviderUtil
                    .convertOBClientRegistrationRequest1JsonStringToISDCRPayload(modifiedPayload);

            if (modifiedPayload == null) {
                log.error("Error occurred while mapping the request payload to the IS DCR API request");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        500,
                        "Internal server error",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            // Set the Content-Type header to application/json
            headers.replace(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);
        }

//        // Generate the Basic Auth header
//        String basicAuthHeader = HttpUtil.generateBasicAuthHeader(isUsername, isPassword);
//
//        // Add the Basic Auth header to the request headers
//        headers.put(HttpHeader.AUTHORIZATION, basicAuthHeader);
//
//        // Add this special header to the request headers in order to bypass the error occurs in the IS that happens
//        // when we sent client TLS cert with the request with basic auth header. This can be removed once the issue
//        // is fixed in the IS.
//        // TODO : Remove this header once the issue is fixed in the IS.
//        headers.put("WSO2-Identity-User", isUsername);

        return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                200,
                modifiedPayload,
                headers,
                null,
                ExtensionResponseStatus.CONTINUE
        );
    }

    @Override
    public ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
        int responseContextDTOStatusCode = responseContextDTO.getStatusCode();

        // If the response status code is not in the 2xx range, then something went wrong from the IS side.
        if (responseContextDTOStatusCode < 200 || responseContextDTOStatusCode >= 300) {
            log.error("Error occurred while processing the request");
            return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                    responseContextDTO.getStatusCode(),
                    "Error occurred while processing the request",
                    null,
                    null,
                    ExtensionResponseStatus.RETURN_ERROR
            );
        }


        HttpMethod httpMethod = HttpMethod.valueOf(responseContextDTO.getMsgInfo().getHttpMethod());

        // If the response is a GET, POST, or PUT request, then the response payload should be validated.
        if (
                httpMethod.equals(HttpMethod.GET) ||
                httpMethod.equals(HttpMethod.POST) ||
                httpMethod.equals(HttpMethod.PUT)
        ) {
            String payload = OpenBankingAPIHandlerUtil.getPayload(responseContextDTO.getMsgInfo());

            if (payload == null) {
                log.error("Response payload is null");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        500,
                        "Internal server error",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }
            OBClientRegistrationResponse1 obClientRegistrationResponse1 = new OBClientRegistrationResponse1(payload);

            String modifiedPayload = null;
            try {
                modifiedPayload = obClientRegistrationResponse1.getOBClientRegistrationResponse1();
            } catch (Exception e) {
                log.error("Error occurred while mapping the response payload to the Open Banking compliant response",
                        e);
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        500,
                        "Internal server error",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            if (modifiedPayload == null) {
                log.error("Error occurred while mapping the response payload to the Open Banking compliant response");
                return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                        500,
                        "Internal server error",
                        null,
                        null,
                        ExtensionResponseStatus.RETURN_ERROR
                );
            }

            return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                    200,
                    modifiedPayload,
                    responseContextDTO.getMsgInfo().getHeaders(),
                    null,
                    ExtensionResponseStatus.CONTINUE
            );
        }

        return OpenBankingAPIHandlerUtil.createExtensionResponseDTO(
                200,
                null,
                responseContextDTO.getMsgInfo().getHeaders(),
                null,
                ExtensionResponseStatus.CONTINUE
        );
    }

    @Override
    public ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
        return null;
    }
}
