package com.wso2.openbanking.uk.gateway.util;

import com.wso2.openbanking.uk.gateway.core.JWTValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains utility methods for Identity Server Information handling.
 */
public class ServiceProviderUtil {
    private static final Log log = LogFactory.getLog(ServiceProviderUtil.class);

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
}
