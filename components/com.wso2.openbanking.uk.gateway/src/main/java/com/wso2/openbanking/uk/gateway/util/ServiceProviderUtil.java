package com.wso2.openbanking.uk.gateway.util;

import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.core.SimpleHttpClient;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import com.wso2.openbanking.uk.common.util.HttpUtil;
import com.wso2.openbanking.uk.gateway.core.JWTValidator;
import com.wso2.openbanking.uk.gateway.exception.JWTValidatorRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains utility methods for Identity Server Information handling.
 */
public class ServiceProviderUtil {
    private static final Log log = LogFactory.getLog(ServiceProviderUtil.class);

    /**
     * This method revokes an access token.
     *
     * @param accessToken the access token to be revoked.
     * @return true if the access token was revoked successfully, false otherwise.
     */
    public static boolean revokeAccessToken(String isHost, String accessToken) {

        // Validate the access token and extract the client id from it.
        JWTValidator accessTokenValidator = new JWTValidator(accessToken);

        if (!accessTokenValidator.validateJwt()) {
            log.warn("Access token validation failed");
            return false;
        }

        String clientId = null;

        try {
            clientId = accessTokenValidator.getClaim("client_id", String.class);
        } catch (JWTValidatorRuntimeException e) {
            log.warn("Error occurred while extracting the client_id from the access token", e);
            return false;
        }

        // Create the payload.

        Map<String, String> payloadMap = new HashMap<String, String>();
        payloadMap.put("token", accessToken);
        payloadMap.put("token_type_hint", "access_token");
        payloadMap.put("client_id", clientId);

        String payload = HttpUtil.convertToXWWWFormUrlEncoded(payloadMap);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        // Sanitize the host URL
        if (isHost.endsWith("/")) {
            isHost = isHost.substring(0, isHost.length() - 1);
        }

        SimpleHttpResponse response = null;

        try {
            response = httpClient.send(
                    new SimpleHttpRequest(
                            HttpMethod.POST,
                            String.format("%s/oauth2/revoke", isHost),
                            payload,
                            new HashMap<String, String>()
                    )
            );
        } catch (Exception e) {
            log.error("Error occurred while revoking the access token", e);
            return false;
        }

        if (response.getStatusCode() != 200) {
            log.error("Error occurred while revoking the access token.");
            return false;
        }

        return true;
    }

    // TODO : Remove the following codes after implementing the Identity Module.

    private static final Set<String> identityServerAcceptingClaims = new HashSet<String>() { {
        add("redirect_uris");
        add("client_name");
        add("client_id");
        add("client_secret");
        add("grant_types");
        add("application_type");
        add("jwks_uri");
        add("url");
        add("ext_param_client_id");
        add("ext_param_client_secret");
        add("contacts");
        add("post_logout_redirect_uris");
        add("request_uris");
        add("response_types");
        add("ext_param_sp_template");
        add("backchannel_logout_uri");
        add("backchannel_logout_session_required");
        add("ext_application_display_name");
        add("token_type_extension");
        add("ext_application_owner");
        add("ext_application_token_lifetime");
        add("ext_user_token_lifetime");
        add("ext_refresh_token_lifetime");
        add("ext_id_token_lifetime");
        add("ext_pkce_mandatory");
        add("ext_pkce_support_plain");
        add("ext_public_client");
        add("token_endpoint_auth_method");
        add("token_endpoint_auth_signing_alg");
        add("sector_identifier_uri");
        add("id_token_signed_response_alg");
        add("id_token_encrypted_response_alg");
        add("id_token_encrypted_response_enc");
        add("software_statement");
        add("request_object_signing_alg");
        add("tls_client_auth_subject_dn");
        add("require_signed_request_object");
        add("require_pushed_authorization_requests");
        add("tls_client_certificate_bound_access_tokens");
        add("subject_type");
        add("request_object_encryption_alg");
        add("request_object_encryption_enc");
    } };

    /**
     * This method converts an OBClientRegistrationRequest1 JSON string to an IS Dynamic Client Registration Payload.
     *
     * @param obClientRegistrationRequest1JsonString the OB Client Registration Request 1 JSON string.
     * @return the ISD Client Registration Payload.
     */
    public static String convertOBClientRegistrationRequest1JsonStringToISDCRPayload(
            String obClientRegistrationRequest1JsonString
    ) {
        // Convert the JSON string to a map
        JSONObject obClientRegistrationRequest1JsonObject = null;
        try {
            obClientRegistrationRequest1JsonObject = (JSONObject) (new JSONParser()).parse(
                    obClientRegistrationRequest1JsonString
            );
        } catch (ParseException e) {
            log.error("Error occurred while parsing the JSON string to a JSON object", e);
            return null;
        }

        JSONObject spDCRRequestClaims = new JSONObject();

        // Iterate through the JSON object and add the claims to the map
        for (Object k : obClientRegistrationRequest1JsonObject.keySet()) {
            String key = (String) k;
            if (identityServerAcceptingClaims.contains(key)) {
                spDCRRequestClaims.put(key, obClientRegistrationRequest1JsonObject.get(key));
            }
        }

        // Set the client_name to the software_id
        spDCRRequestClaims.put("client_name", obClientRegistrationRequest1JsonObject.get("software_id"));

        spDCRRequestClaims.put("tls_client_certificate_bound_access_tokens", true);
        spDCRRequestClaims.put("token_type_extension", "JWT");

        // Extract the JWKS URI from the software statement and add it to the map
        String softwareStatement = (String) obClientRegistrationRequest1JsonObject.get("software_statement");

        JWTValidator softwareStatementJwtValidator = new JWTValidator(softwareStatement);

        if (softwareStatement != null) {
            try {
                String jwksUri = (String) softwareStatementJwtValidator.getClaim(
                        "software_jwks_endpoint", String.class
                        );
                if (jwksUri != null) {
                    spDCRRequestClaims.put("jwks_uri", jwksUri);
            }
            } catch (Exception e) {
                log.error("Error occurred while extracting the JWKS URI from the software statement", e);
            }
        }

        return spDCRRequestClaims.toJSONString();
    }


    /**
     * This method converts the response came from the Identity Server DCR endpoint to UK spec compliant DCR response.
     *
     * @return The UK spec complaint DCR response as a JSON string.
     */
    public static String convertISDCRResponseJsonStringToOBClientRegistrationResponse1(
            String spServiceProviderResponse
    ) {
        JSONObject responseJsonObject = null;
        try {
            responseJsonObject = (JSONObject) (new JSONParser()).parse(spServiceProviderResponse);
        } catch (ParseException e) {
            log.error("Error occurred while parsing the JSON string to a JSON object", e);
            return null;
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
            log.error("Error occurred while parsing the software statement", e);
            return null;
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
