package com.wso2.openbanking.uk.gateway.core;


import com.wso2.openbanking.uk.common.constants.HttpHeader;
import com.wso2.openbanking.uk.common.constants.HttpHeaderContentType;
import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.core.SimpleAbstractHttpClient;
import com.wso2.openbanking.uk.common.exception.GatewayHttpClientRuntimeException;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import com.wso2.openbanking.uk.common.util.HttpUtil;
import com.wso2.openbanking.uk.common.util.StringUtil;
import com.wso2.openbanking.uk.gateway.model.DevPortalApplication;
import com.wso2.openbanking.uk.gateway.exception.DevPortalRestApiManagerRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.lang.reflect.Array;
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

    private final SimpleAbstractHttpClient client;
    private final String amHost;
    private final String clientRegistrationUrl;

    private final String amUsername;
    private final String amPassword;

    private String clientId;
    private String clientSecret;

    private String accessToken;

    public DevPortalRestApiManager(
            SimpleAbstractHttpClient client,
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

    public DevPortalRestApiManager(SimpleAbstractHttpClient client) {
        this(client, null, null, null);
    }

    public DevPortalRestApiManager(SimpleAbstractHttpClient client, String amHost) {
        this(client, amHost, null, null);
    }

    public DevPortalApplication createApplication(DevPortalApplication application) throws DevPortalRestApiManagerRuntimeException {
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
        headers.put(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.POST,
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

    public DevPortalApplication retrieveApplication(String applicationId) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.GET,
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

    public List<DevPortalApplication> searchApplicationsByName(String applicationName) throws DevPortalRestApiManagerRuntimeException {
        authenticate();

        Map<String, String> params = new HashMap<>();
        params.put("query", applicationName);

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.GET,
                    HttpUtil.concatParamsToUrl(amHost + REST_API_RESOURCE_APPLICATION, params),
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

        List<DevPortalApplication> applications = new ArrayList<>();

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
        headers.put(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.POST,
                    amHost + REST_API_RESOURCE_APPLICATION + "/" + applicationId + "/map-keys",
                    (new JSONObject(body)).toJSONString(),
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to map the application keys", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to map the application keys", e);
        }

        // handleExpectedHttpStatusResponse(response, 200, "Failed to map the application keys");

        // NOTE : The APIM response status code is 500 when the key mapping is successful
        if (!(response.getStatusCode() == 200 || response.getStatusCode() == 500)) {
            String error = String.format(
                    "Failed to map the application keys. Status Code : %d | Response Body : %s",
                    response.getStatusCode(),
                    response.getBody()
            );
            throw new DevPortalRestApiManagerRuntimeException(error);
        }

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

//        if (keyMappingId == null) {
//            String error = "Failed to map the application keys : Key Mapping ID is null";
//            log.error(error);
//            throw new DevPortalRestApiManagerRuntimeException(error);
//        }

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
        headers.put(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.POST,
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
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.DELETE,
                    amHost + REST_API_RESOURCE_SUBSCRIPTION + "/" + subscriptionId,
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to unsubscribe to API", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to unsubscribe to API", e);
        }
    }

    public List<String> getSubscriptionsByApplicationId(String applicationId) {
        authenticate();

        Map<String, String> params = new HashMap<>();
        params.put("applicationId", applicationId);

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.GET,
                    HttpUtil.concatParamsToUrl(amHost + REST_API_RESOURCE_SUBSCRIPTION, params),
                    null,
                    headers
            ));
        } catch (GatewayHttpClientRuntimeException e) {
            log.error("Failed to get all subscriptions of an application", e);
            throw new DevPortalRestApiManagerRuntimeException("Failed to get all subscriptions of an application", e);
        }

        handleExpectedHttpStatusResponse(response, 200, "Failed to get all subscriptions of an application");

        JSONObject responseJson = null;

        try {
            responseJson = (JSONObject) (new JSONParser()).parse(response.getBody());
        } catch (ParseException e) {
            log.error("Error parsing the response", e);
            throw new DevPortalRestApiManagerRuntimeException("Error parsing the response", e);
        }

        JSONArray subscriptionsJson = (JSONArray) responseJson.get("list");

        List<String> subscriptionIds = new ArrayList<>();

        for (Object subscription : subscriptionsJson) {
            subscriptionIds.add((String) ((JSONObject) subscription).get("subscriptionId"));
        }

        return subscriptionIds;
    }

    public List<String> searchAPIsByTag(String tag) {
        authenticate();

        Map<String, String> params = new HashMap<>();
        params.put("query", String.format("tag:%s", tag));

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.GET,
                    HttpUtil.concatParamsToUrl(amHost + REST_API_RESOURCE_APIS, params),
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

    public DevPortalApplication updateApplication(DevPortalApplication application) throws DevPortalRestApiManagerRuntimeException {
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
        headers.put(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.PUT,
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
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBearerAuthHeader(accessToken));
        headers.put(HttpHeader.ACCEPT, HttpHeaderContentType.APPLICATION_JSON);

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.DELETE,
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
        headers.put(HttpHeader.CONTENT_TYPE, HttpHeaderContentType.APPLICATION_JSON);
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBasicAuthHeader(amUsername, amPassword));

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                            HttpMethod.POST,
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
        headers.put(HttpHeader.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.put(HttpHeader.AUTHORIZATION, HttpUtil.generateBasicAuthHeader(clientId, clientSecret));

        SimpleHttpResponse response = null;

        try {
            response = client.send(new SimpleHttpRequest(
                    HttpMethod.POST,
                    amHost + REST_API_RESOURCE_TOKEN,
                    HttpUtil.convertToXWWWFormUrlEncoded(body),
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

    private static void handleExpectedHttpStatusResponse(
            SimpleHttpResponse response,
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
            // log.error(StringUtil.sanitizeString(error));
            throw new DevPortalRestApiManagerRuntimeException(StringUtil.sanitizeString(error));
        }
    }

    private static DevPortalApplication mapJsonObjectToAPIMApplication(JSONObject jsonObject) {
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

        return new DevPortalApplication(
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
