package com.wso2.openbanking.uk.gateway.core.handler.dcr.devportal;


import com.wso2.openbanking.uk.gateway.common.gatewayhttpclient.GatewayAbstractHttpClient;
import com.wso2.openbanking.uk.gateway.common.gatewayhttpclient.GatewayHttpClientRuntimeException;
import com.wso2.openbanking.uk.gateway.common.gatewayhttpclient.GatewayHttpRequest;
import com.wso2.openbanking.uk.gateway.common.gatewayhttpclient.GatewayHttpResponse;
import com.wso2.openbanking.uk.gateway.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DevPortalRestApiManager {
    private static final Log log = LogFactory.getLog(DevPortalRestApiManager.class);

    private static final String DEFAULT_AM_HOST = "https://localhost:9443";
    private static final String REST_API_RESOURCE_CLIENT_REGISTRATION = "/client-registration/v0.17/register";
    private static final String REST_API_RESOURCE_TOKEN = "/oauth2/token";
    private static final String REST_API_RESOURCE_APPLICATION = "/api/am/devportal/v3/applications";
    private static final String REST_API_RESOURCE_SUBSCRIPTION = "/api/am/devportal/v3/subscriptions";
    private static final String REST_API_RESOURCE_APIS = "/api/am/devportal/v3/apis";

    private static final String DEFAULT_AM_USERNAME = "admin";
    private static final String DEFAULT_AM_PASSWORD = "admin";

    private static final String AM_APPLICATION_CLIENT_NAME = "OpenBankingUK-DevPortalRestApiManager";

    private final GatewayAbstractHttpClient client;
    private final String amHost;
    private final String clientRegistrationUrl;

    private final String amUsername;
    private final String amPassword;

    private String clientId;
    private String clientSecret;

    private String accessToken;

    public DevPortalRestApiManager(
            GatewayAbstractHttpClient client,
            String amHost,
            String amUsername,
            String amPassword
    ) {
        this.client = client;
        this.amHost = amHost == null || amHost.isBlank() ? DEFAULT_AM_HOST : amHost;

        this.clientRegistrationUrl = this.amHost + REST_API_RESOURCE_CLIENT_REGISTRATION;

        // TODO : Get these from the AM configurations
        this.amUsername = amUsername == null || amUsername.isBlank() ? DEFAULT_AM_USERNAME : amUsername;
        this.amPassword = amPassword == null || amPassword.isBlank() ? DEFAULT_AM_PASSWORD : amPassword;
    }

    public DevPortalRestApiManager(GatewayAbstractHttpClient client) {
        this(client, null, null, null);
    }

    public DevPortalRestApiManager(GatewayAbstractHttpClient client, String amHost) {
        this(client, amHost, null, null);
    }

    public APIMApplication createApplication(APIMApplication application) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, Object> body = new HashMap<>();
        body.put("name", application.getName());
        body.put("throttlingPolicy", application.getThrottlingPolicy());
        body.put("description", application.getDescription());
        body.put("tokenType", application.getTokenType());
        body.put("groups", application.getGroups());
        body.put("attributes", application.getAttributes());
        body.put("subscriptionScopes", application.getSubscriptionScopes());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "POST",
                    amHost + REST_API_RESOURCE_APPLICATION,
                    (new JSONObject(body)).toJSONString(),
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to create the application", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to create the application", e);
        }

        handleExpectedHttpStatusResponse(response, 201, "Failed to create the application");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        return mapJsonObjectToAPIMApplication(responseJson);
    }

    public APIMApplication retrieveApplication(String applicationId) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "GET",
                    amHost + REST_API_RESOURCE_APPLICATION + "/" + applicationId,
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to retrieve the application", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to retrieve the application", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to retrieve the application");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        return mapJsonObjectToAPIMApplication(responseJson);
    }

    public List<APIMApplication> searchApplicationsByName(String applicationName) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, String> params = new HashMap<>();
        params.put("query", applicationName);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "GET",
                    concatParamsToUrl(amHost + REST_API_RESOURCE_APPLICATION, params),
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to search applications by name", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to search applications by name", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to search applications by name");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        JSONArray applicationsJson = (JSONArray) responseJson.get("list");

        List<APIMApplication> applications = new ArrayList<>();

        for (Object application : applicationsJson) {
            applications.add(mapJsonObjectToAPIMApplication((JSONObject) application));
        }

        return applications;
    }

    public String mapApplicationKeys(
            String applicationId,
            String consumerKey,
            String consumerSecret,
            String keyManager,
            boolean isSandbox
    ) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        String keyType = isSandbox ? "SANDBOX" : "PRODUCTION";

        Map<String, Object> body = new HashMap<>();
        body.put("consumerKey", consumerKey);
        body.put("consumerSecret", consumerSecret);
        body.put("keyManager", keyManager);
        body.put("keyType", keyType);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "POST",
                    amHost + REST_API_RESOURCE_APPLICATION + "/" + applicationId + "/map-keys",
                    (new JSONObject(body)).toJSONString(),
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to map the application keys", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to map the application keys", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to map the application keys");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

//        String mode = (String) responseJson.get("mode");
//
//        if (!mode.equals("MAPPED")) {
//            String error = StringUtil.sanitizeString(String.format(
//                    "Failed to map the application keys. Mode : %s", mode
//                    ));
//            log.error(error);
//            throw new DevPortalRestApiManagerRuntimeException(error);
//        }

//        String keyState = (String) responseJson.get("keyState");
//
//        if (!keyState.equals("APPROVED")) {
//            String error = StringUtil.sanitizeString(String.format(
//                    "Failed to map the application keys. Key State : %s", keyState
//            ));
//            log.error(error);
//            throw new DevPortalRestApiManagerRuntimeException(error);
//        }

        String keyMappingId = (String) responseJson.get("keyMappingId");

        if (keyMappingId == null) {
            String error = "Failed to map the application keys : Key Mapping ID is null";
            log.error(error);
            throw new DevPortalRestApiManagerRuntimeException(error);
        }

        return keyMappingId;
    }

    public List<String> subscribeToAPIs(String applicationId, String[] apiIds) {
        authenticate();

        JSONArray body = new JSONArray();

        for (String apiId : apiIds) {
            JSONObject subscriptionBody = new JSONObject();
            subscriptionBody.put("applicationId", applicationId);
            subscriptionBody.put("apiId", apiId);
            subscriptionBody.put("throttlingPolicy", "Unlimited");
            body.add(subscriptionBody);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "POST",
                    amHost + REST_API_RESOURCE_SUBSCRIPTION + "/multiple",
                    body.toJSONString(),
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to subscribe to APIs", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to subscribe to APIs", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to subscribe to APIs");

        JSONArray responseJson = null;

        try {
            responseJson = (JSONArray) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        List<String> subscriptionIds = new ArrayList<>();

        for (Object subscription : responseJson) {
            subscriptionIds.add((String) ((JSONObject) subscription).get("subscriptionId"));
        }

        return subscriptionIds;
    }

    public void unsubscribeToAPI(String subscriptionId) {
        authenticate();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "DELETE",
                    amHost + REST_API_RESOURCE_SUBSCRIPTION + "/" + subscriptionId,
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to unsubscribe to API", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to unsubscribe to API", e);
        }
    }

    public List<String> searchAPIsByTag(String tag) {
        authenticate();

        Map<String, String> params = new HashMap<>();
        params.put("query", String.format("tag:%s", tag));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "GET",
                    concatParamsToUrl(amHost + REST_API_RESOURCE_APIS, params),
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to search APIs by tag", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to search APIs by tag", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to search APIs by tag");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        JSONArray apisJson = (JSONArray) responseJson.get("list");

        List<String> apiIds = new ArrayList<>();

        for (Object api : apisJson) {
            apiIds.add((String) ((JSONObject) api).get("id"));
        }

        return apiIds;
    }

    public APIMApplication updateApplication(APIMApplication application) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, Object> body = new HashMap<>();
        body.put("name", application.getName());
        body.put("throttlingPolicy", application.getThrottlingPolicy());
        body.put("description", application.getDescription());
        body.put("tokenType", application.getTokenType());
        body.put("groups", application.getGroups());
        body.put("attributes", application.getAttributes());
        body.put("subscriptionScopes", application.getSubscriptionScopes());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "PUT",
                    amHost + REST_API_RESOURCE_APPLICATION + "/" + application.getApplicationId(),
                    (new JSONObject(body)).toJSONString(),
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to update the application", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to update the application", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to update the application");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        return mapJsonObjectToAPIMApplication(responseJson);
    }

    public void deleteApplication(String applicationId) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", generateBearerAuthHeader(accessToken));
        headers.put("Accept", "application/json");

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "DELETE",
                    amHost + REST_API_RESOURCE_APPLICATION + "/" + applicationId,
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to delete the application", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to delete the application", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to delete the application");
    }

    private void authenticate() throws DevPortalRestApiManagerRuntimeException {
        registerClient();
        generateTokens();
    }

    private void registerClient() throws DevPortalRestApiManagerRuntimeException {
        Map<String, Object> body = new HashMap<>();
        body.put("callbackUrl", amHost);
        body.put("clientName", AM_APPLICATION_CLIENT_NAME);
        body.put("owner", amUsername);
        body.put("grantType", "client_credentials password refresh_token");
        body.put("saasApp", true);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", generateBasicAuthHeader(amUsername, amPassword));

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                            "POST",
                            clientRegistrationUrl,
                            (new JSONObject(body)).toJSONString(),
                            headers
                    ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to register the client", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to register the client", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to register the client");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        clientId = (String) responseJson.get("clientId");
        clientSecret = (String) responseJson.get("clientSecret");
    }

    private void generateTokens() throws DevPortalRestApiManagerRuntimeException {
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "password");
        body.put("username", amUsername);
        body.put("password", amPassword);
        body.put("scope", "openid apim:subscribe apim:app_manage");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", generateBasicAuthHeader(clientId, clientSecret));

        GatewayHttpResponse response = null;

        try {
            response = client.send(new GatewayHttpRequest(
                    "POST",
                    amHost + REST_API_RESOURCE_TOKEN,
                    convertToXWWWFormUrlEncoded(body),
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to generate tokens", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to generate tokens", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to generate tokens");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        accessToken = (String) responseJson.get("access_token");
    }

    private static String convertToXWWWFormUrlEncoded(Map<String, String> data) {
        StringBuilder encodedData = new StringBuilder();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            encodedData.append(entry.getKey());
            encodedData.append("=");
            encodedData.append(entry.getValue());
            encodedData.append("&");
        }

        return encodedData.toString();
    }

    private static String concatParamsToUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey());
            urlBuilder.append("=");
            urlBuilder.append(entry.getValue());
            urlBuilder.append("&");
        }

        return urlBuilder.toString();
    }

    private static String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + new String(
                Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
    }

    private static String generateBearerAuthHeader(String token) {
        return "Bearer " + token;
    }

    private static void handleExpectedHttpStatusResponse(
            GatewayHttpResponse response,
            int expectedHttpStatus,
            String errorMessage
    ) throws DevPortalRestApiManagerRuntimeException {
        if (response.getStatusCode() != expectedHttpStatus) {
            String error = String.format(
              "Expected status code %d, but received %d. Response Body : %s | %s",
              expectedHttpStatus,
              response.getStatusCode(),
              response.getBody(),
              errorMessage
            );
            log.error(error);
            throw new DevPortalRestApiManagerRuntimeException(error);
        }
    }

    private static APIMApplication mapJsonObjectToAPIMApplication(JSONObject jsonObject) {
        // Convert the response attributes to Map<String, String>
        Map<String, String> attributes = new HashMap<>();
        JSONObject attributesJson = (JSONObject) jsonObject.get("attributes");
        for (Object key : attributesJson.keySet()) {
            attributes.put((String) key, (String) attributesJson.get(key));
        }

        // Convert the groups to String[]
        String[] groups = null;

        if (jsonObject.get("groups") != null) {
            groups = convertJsonArrayToArray((JSONArray) jsonObject.get("groups"), String.class);
        }

        // Convert the subscriptionScopes to String[]
        String[] subscriptionScopes = null;

        if (jsonObject.get("subscriptionScopes") != null) {
            subscriptionScopes = convertJsonArrayToArray(
                    (JSONArray) jsonObject.get("subscriptionScopes"),
                    String.class
            );
        }

        return new APIMApplication(
                (String) jsonObject.get("applicationId"),
                (String) jsonObject.get("name"),
                (String) jsonObject.get("throttlingPolicy"),
                (String) jsonObject.get("description"),
                (String) jsonObject.get("tokenType"),
                groups,
                attributes,
                subscriptionScopes
        );
    }

    static <T> T[] convertJsonArrayToArray(JSONArray jsonArray, Class<T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, jsonArray.size());
        for (int i = 0; i < array.length; i++) {
            array[i] = (T) jsonArray.get(i);
        }
        return array;
    }
}
