package com.wso2.openbanking.uk.gateway.core;

import com.wso2.openbanking.uk.gateway.exception.OpenBankingAPIHandlerRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class responsible for converting the response came from the Identity Server DCR endpoint to UK spec compliant
 * DCR response. OBClientRegistrationResponse1 to be exact.
 */
public class OBClientRegistrationResponse1 {
    private final String spServiceProviderResponse;

    /**
     * Construct an OBClientRegistrationResponse1 object.
     *
     * @param spServiceProviderResponse The response returned from the IS DCR endpoint. This MUST be a JSON String.
     */
    public OBClientRegistrationResponse1(String spServiceProviderResponse) {
        this.spServiceProviderResponse = spServiceProviderResponse;
    }

    /**
     * @return Returns the IS DCR response given to the object at the creation.
     */
    public String getSpServiceProviderResponse() {
        return spServiceProviderResponse;
    }

    /**
     * @return The UK spec complaint DCR response as a JSON string.
     * @throws OpenBankingAPIHandlerRuntimeException If the provided response, or its software statement cause any
     *                                               parsing exceptions.
     */
    public String getOBClientRegistrationResponse1() throws OpenBankingAPIHandlerRuntimeException {
        JSONObject responseJsonObject = null;
        try {
            responseJsonObject = (JSONObject) (new JSONParser()).parse(this.spServiceProviderResponse);
        } catch (ParseException e) {
            throw new OpenBankingAPIHandlerRuntimeException("Error while parsing the response from the SP", e);
        }

        // Decode the software statement from the SP response
        String softwareStatement = (String) responseJsonObject.get("software_statement");
        JWTValidator softwareStatementJwtValidator = new JWTValidator(softwareStatement);

        JSONObject softwareStatementJsonObject = null;
        try {
            softwareStatementJsonObject = (JSONObject) (new JSONParser()).parse(
                    softwareStatementJwtValidator.getJSONString()
            );
        } catch (ParseException e) {
            throw new OpenBankingAPIHandlerRuntimeException("Error while parsing the software statement", e);
        }

        // Create the OB Client Registration Response 1
        JSONObject obClientRegistrationResponse1 = new JSONObject();

        // Client Id
        String clientId = (String) responseJsonObject.get("client_id");
        obClientRegistrationResponse1.put("client_id", clientId);

        // Client Secret
        String clientSecret = (String) responseJsonObject.get("client_secret");
        obClientRegistrationResponse1.put("client_secret", clientSecret);

        // Client Id Issued At
        // TODO : This is a temporary solution. Need to get the actual value from the SP response.
        // Unfortunately, the SP response does not contain the client_id_issued_at value or any equivalent value.
        long clientIdIssuedAt = System.currentTimeMillis() / 1000L;
        obClientRegistrationResponse1.put("client_id_issued_at", clientIdIssuedAt);

        // Client Secret Expires At
        long clientSecretExpiresAt = responseJsonObject.get("client_secret_expires_at") != null ?
                (long) responseJsonObject.get("client_secret_expires_at") : 0;
        obClientRegistrationResponse1.put("client_secret_expires_at", clientSecretExpiresAt);

        // Redirect URIs
        JSONArray redirectUris = (JSONArray) responseJsonObject.get("redirect_uris");
        obClientRegistrationResponse1.put("redirect_uris", redirectUris);

        // Token Endpoint Auth Method
        String tokenEndpointAuthMethod = (String) responseJsonObject.get("token_endpoint_auth_method");
        obClientRegistrationResponse1.put("token_endpoint_auth_method", tokenEndpointAuthMethod);

        // Grant Types
        JSONArray grantTypes = (JSONArray) responseJsonObject.get("grant_types");
        obClientRegistrationResponse1.put("grant_types", grantTypes);

        // Response Types
        // NOTE :   The response_types value is not available in the SP response. Need to get the value from
        //          the software statement. If the value is not available in the software statement, set it to empty.
        JSONArray responseTypes = resolveFromResponseOrSoftwareStatement(
                responseJsonObject,
                softwareStatementJsonObject,
                "response_types",
                new JSONArray()
        );
        obClientRegistrationResponse1.put("response_types", responseTypes);

        // Software Id
        String softwareId = (String) responseJsonObject.get("client_name");
        obClientRegistrationResponse1.put("software_id", softwareId);

        // Scope
        // NOTE :   Look for scopes in the software statement. If not available, set it to empty.
        JSONArray scopes = (JSONArray) softwareStatementJsonObject.get("scope");
        obClientRegistrationResponse1.put("scope", scopes);

        // Software Statement
        obClientRegistrationResponse1.put("software_statement", softwareStatement);

        // Application Type
        // NOTE :   Look for application_type in the software statement. If not available, set it to "web".
        String applicationType = resolveFromResponseOrSoftwareStatement(
                responseJsonObject,
                softwareStatementJsonObject,
                "application_type",
                null
        );
        obClientRegistrationResponse1.put("application_type", applicationType);

        // Id token signed response alg
        String idTokenSignedResponseAlg = (String) responseJsonObject.get("id_token_signed_response_alg");
        obClientRegistrationResponse1.put("id_token_signed_response_alg", idTokenSignedResponseAlg);

        // Token Endpoint Auth Signing Alg
        String tokenEndpointAuthSigningAlg = (String) responseJsonObject.get("token_endpoint_auth_signing_alg");
        obClientRegistrationResponse1.put("token_endpoint_auth_signing_alg", tokenEndpointAuthSigningAlg);

        // Request Object Signing Alg
        String requestObjectSigningAlg = (String) responseJsonObject.get("request_object_signing_alg");
        obClientRegistrationResponse1.put("request_object_signing_alg", requestObjectSigningAlg);

        // TLS Client Auth Subject DN
        String tlsClientAuthSubjectDN = (String) responseJsonObject.get("tls_client_auth_subject_dn");
        obClientRegistrationResponse1.put("tls_client_auth_subject_dn", tlsClientAuthSubjectDN);

        // Back Channel Token Delivery Mode
        // NOTE :   Look for backchannel_token_delivery_mode in the request.
        //          If not look in the software statement. Otherwise, set it to "poll".
        String backChannelTokenDeliveryMode = resolveFromResponseOrSoftwareStatement(
                responseJsonObject,
                softwareStatementJsonObject,
                "backchannel_token_delivery_mode",
                "poll"
        );
        obClientRegistrationResponse1.put("backchannel_token_delivery_mode", backChannelTokenDeliveryMode);

        // Back Channel Authentication Request Signing Alg
        // backchannel_client_notification_endpoint
        String backChannelClientNotificationEndpoint = resolveFromResponseOrSoftwareStatement(
                responseJsonObject,
                softwareStatementJsonObject,
                "backchannel_client_notification_endpoint",
                null
        );
        obClientRegistrationResponse1.put(
                "backchannel_client_notification_endpoint",
                backChannelClientNotificationEndpoint
        );

        // Back Channel Authentication Request Signing Alg
        String backChannelAuthenticationRequestSigningAlg = resolveFromResponseOrSoftwareStatement(
                responseJsonObject,
                softwareStatementJsonObject,
                "backchannel_authentication_request_signing_alg",
                null
        );
        obClientRegistrationResponse1.put(
                "backchannel_authentication_request_signing_alg",
                backChannelAuthenticationRequestSigningAlg
        );

        // Back Channel User Code Parameter Supported
        boolean backChannelUserCodeParameterSupported = resolveFromResponseOrSoftwareStatement(
                responseJsonObject,
                softwareStatementJsonObject,
                "backchannel_user_code_parameter_supported",
                false
        );

        obClientRegistrationResponse1.put(
                "backchannel_user_code_parameter_supported",
                backChannelUserCodeParameterSupported
        );


        return obClientRegistrationResponse1.toJSONString();
    }

    private static <T> T resolveFromResponseOrSoftwareStatement(
            JSONObject request,
            JSONObject softwareStatement,
            String key,
            T defaultValue
    ) {
        T result = null;

        if (request != null && request.get(key) != null) {
            result = (T) request.get(key);
        } else if (softwareStatement != null && softwareStatement.get(key) != null) {
            result = (T) softwareStatement.get(key);
        } else {
            result = defaultValue;
        }

        return result;
    }
}
