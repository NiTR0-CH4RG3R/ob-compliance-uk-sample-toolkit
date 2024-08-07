package com.wso2.openbanking.uk.gateway.core;

import com.wso2.openbanking.uk.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private static final Log log = LogFactory.getLog(ServiceProvider.class);

    private static class RequestClaimProperty {
        final Class<?> type;
        final boolean required;
        final String equivalentKey;

        public <T> RequestClaimProperty(Class<T> type, boolean required, String equivalentKey) {
            this.type = type;
            this.required = required;
            this.equivalentKey = equivalentKey;
        }

        public <T> RequestClaimProperty(Class<T> type, boolean required) {
            this(type, required, null);
        }
    }

    private static final Map<String, RequestClaimProperty> requestClaimPropertyMap = new HashMap<>() {{
        put("redirect_uris", new RequestClaimProperty(String[].class, true));
        put("client_name", new RequestClaimProperty(String.class, true, "software_id"));
        put("client_id", new RequestClaimProperty(String.class, false));
        put("client_secret", new RequestClaimProperty(String.class, false));
        put("grant_types", new RequestClaimProperty(String[].class, false));
        put("application_type", new RequestClaimProperty(String.class, false));
        put("jwks_uri", new RequestClaimProperty(String.class, false));
        put("url", new RequestClaimProperty(String.class, false));
        put("ext_param_client_id", new RequestClaimProperty(String.class, false));
        put("ext_param_client_secret", new RequestClaimProperty(String.class, false));
        put("contacts", new RequestClaimProperty(String[].class, false));
        put("post_logout_redirect_uris", new RequestClaimProperty(String[].class, false));
        put("request_uris", new RequestClaimProperty(String[].class, false));
        put("response_types",  new RequestClaimProperty(String[].class, false));
        put("ext_param_sp_template", new RequestClaimProperty(String.class, false));
        put("backchannel_logout_uri", new RequestClaimProperty(String.class, false));
        put("backchannel_logout_session_required", new RequestClaimProperty(Boolean.class, false));
        put("ext_application_display_name", new RequestClaimProperty(String.class, false));
        put("token_type_extension", new RequestClaimProperty(String.class, false));
        put("ext_application_owner", new RequestClaimProperty(String.class, false));
        put("ext_application_token_lifetime", new RequestClaimProperty(Integer.class, false));
        put("ext_user_token_lifetime", new RequestClaimProperty(Integer.class, false));
        put("ext_refresh_token_lifetime", new RequestClaimProperty(Integer.class, false));
        put("ext_id_token_lifetime", new RequestClaimProperty(Integer.class, false));
        put("ext_pkce_mandatory", new RequestClaimProperty(Boolean.class, false));
        put("ext_pkce_support_plain", new RequestClaimProperty(Boolean.class, false));
        put("ext_public_client", new RequestClaimProperty(Boolean.class, false));
        put("token_endpoint_auth_method", new RequestClaimProperty(String.class, false));
        put("token_endpoint_auth_signing_alg", new RequestClaimProperty(String.class, false));
        put("sector_identifier_uri", new RequestClaimProperty(String.class, false));
        put("id_token_signed_response_alg", new RequestClaimProperty(String.class, false));
        put("id_token_encrypted_response_alg", new RequestClaimProperty(String.class, false));
        put("id_token_encrypted_response_enc", new RequestClaimProperty(String.class, false));
        put("software_statement", new RequestClaimProperty(String.class, false));
        put("request_object_signing_alg", new RequestClaimProperty(String.class, false));
        put("tls_client_auth_subject_dn", new RequestClaimProperty(String.class, false));
        put("require_signed_request_object", new RequestClaimProperty(Boolean.class, false));
        put("require_pushed_authorization_requests", new RequestClaimProperty(Boolean.class, false));
        put("tls_client_certificate_bound_access_tokens", new RequestClaimProperty(Boolean.class, false));
        put("subject_type", new RequestClaimProperty(String.class, false));
        put("request_object_encryption_alg", new RequestClaimProperty(String.class, false));
        put("request_object_encryption_enc", new RequestClaimProperty(String.class, false));
    }};


    public ServiceProvider() {

    }

    public static String convertJsonStringToISDCRRequestJsonString(String jsonString) {
        // Convert the JSON string to a map
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) (new JSONParser()).parse(jsonString);
        } catch (ParseException e) {
            log.error("Error occurred while parsing the JSON string to a JSON object", e);
            return null;
        }

        Map<String, Object> spDCRRequestClaims = new HashMap<>();
        for (Map.Entry<String, RequestClaimProperty> entry : requestClaimPropertyMap.entrySet()) {
            String key = entry.getKey();
            RequestClaimProperty requestClaimProperty = entry.getValue();
            Class<?> type = requestClaimProperty.type;
            boolean required = requestClaimProperty.required;

            if (!jsonObject.containsKey(key)) {
                if (required) {
                    if (requestClaimProperty.equivalentKey != null) {
                        String equivalentKey = requestClaimProperty.equivalentKey;
                        if (jsonObject.containsKey(equivalentKey) && jsonObject.get(equivalentKey) != null) {
                            spDCRRequestClaims.put(key, jsonObject.get(equivalentKey));
                            continue;
                        }
                    }

//                    log.error(String.format("Required claim is missing: %s",
//                            StringUtil.sanitizeString(key)));
                    return null;
                }

//                log.warn(String.format("Optional claim is missing: %s",
//                        StringUtil.sanitizeString(key)));
                continue;
            }

            Object value = jsonObject.get(key);
            if (!type.isInstance(value)) {
//                log.warn(String.format("Invalid claim type for claim: %s. Expected %s, but found %s",
//                        StringUtil.sanitizeString(key), type.getSimpleName(), value.getClass().getSimpleName()));
            }

            spDCRRequestClaims.put(key, value);
        }

        spDCRRequestClaims.put("tls_client_certificate_bound_access_tokens", true);

        return (new JSONObject(spDCRRequestClaims)).toJSONString();
    }
}
