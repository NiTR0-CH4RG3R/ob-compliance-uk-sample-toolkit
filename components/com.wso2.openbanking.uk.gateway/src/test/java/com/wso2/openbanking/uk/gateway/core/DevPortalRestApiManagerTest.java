package com.wso2.openbanking.uk.gateway.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wso2.openbanking.uk.common.core.SimpleHttpClient;
import com.wso2.openbanking.uk.gateway.model.DevPortalApplication;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * This class tests the DevPortalRestApiManager.
 */
public class DevPortalRestApiManagerTest {

    private DevPortalRestApiManager devPortalRestApiManager;

    private String applicationId = null;
    private final String applicationName = "TestApplication10";
    private final String applicationThrottlingPolicy = "Unlimited";
    private final String applicationDescription = "description";
    private final String applicationTokenType = "JWT";

    private final String consumerKey = "a7fvPXfZzXfbZIy__nYzBOY8pnEa";
    private final String consumerSecret = "02sv1TtFBQbfP0JOfViJ2ENVjORSK1f1Ily7fyw3sfoa";

    private final String tag = "regulatory";
    private final String expectedApiId = "d356e142-bd87-49fa-805f-771582c9a34f";

    private String keyMappingId = null;
    private String subscriptionId = null;

    InetSocketAddress address = new InetSocketAddress(8000);
    HttpServer httpServer = null;

    @BeforeClass
    public void setUp() throws NoSuchAlgorithmException, KeyManagementException {
        // Bypass SSL certificate in order to test the signature validation using JWKSetURL. This is not required for
        // production code as we are already using openbanking.atlassian issued root and issuer certificates.
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) { }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) { }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        }, new java.security.SecureRandom());
        SSLContext.setDefault(sslContext);

        try {
            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpHandler clientCreationHandler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = ("{\n" +
                        "    \"clientId\": \"lHQcQZv4JCbx8gLlWDPUylKEURwa\",\n" +
                        "    \"clientName\": \"KEY_MAPPING_TEST\",\n" +
                        "    \"callBackURL\": null,\n" +
                        "    \"clientSecret\": \"4tR5IG6xyKaOsPOhPztjoZvX2M8a\",\n" +
                        "    \"isSaasApplication\": true,\n" +
                        "    \"appOwner\": \"admin\",\n" +
                        "    \"jsonString\": \"{\\\"grant_types\\\":\\\"client_credentials password refresh_token\\\"}\",\n" +
                        "    \"jsonAppAttribute\": \"{}\",\n" +
                        "    \"applicationUUID\": null,\n" +
                        "    \"tokenType\": \"DEFAULT\"\n" +
                        "}").getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/client-registration/v0.17/register", clientCreationHandler);

        HttpHandler accessTokenHandler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = ("{\n" +
                        "    \"access_token\": \"d4b5d338-a7ab-3974-b869-a29d90f96afa\",\n" +
                        "    \"refresh_token\": \"5c8985dc-1c22-3da5-a221-f1d7e6e75efd\",\n" +
                        "    \"scope\": \"apim:app_manage apim:subscribe\",\n" +
                        "    \"token_type\": \"Bearer\",\n" +
                        "    \"expires_in\": 3600\n" +
                        "}").getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/oauth2/token", accessTokenHandler);

        HttpHandler appCreation = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = ("{\n" +
                        "    \"applicationId\": \"67e6cd9b-e411-401a-9f77-fcdfaf37aa9e\",\n" +
                        "    \"name\": \"" + applicationName + "\",\n" +
                        "    \"throttlingPolicy\": \"" + applicationThrottlingPolicy + "\",\n" +
                        "    \"description\": \"" + applicationDescription + "\",\n" +
                        "    \"tokenType\": \"" + applicationTokenType + "\",\n" +
                        "    \"status\": \"APPROVED\",\n" +
                        "    \"groups\": [],\n" +
                        "    \"subscriptionCount\": 0,\n" +
                        "    \"keys\": [],\n" +
                        "    \"attributes\": {},\n" +
                        "    \"subscriptionScopes\": [],\n" +
                        "    \"owner\": \"admin\",\n" +
                        "    \"hashEnabled\": null,\n" +
                        "    \"createdTime\": \"1723099288000\",\n" +
                        "    \"updatedTime\": \"1723099288000\"\n" +
                        "}").getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/api/am/devportal/v3/applications", appCreation);


        httpServer.start();

        String amHost = "https://localhost:8000";
        String username = "admin";
        String password = "admin";

        SimpleHttpClient gatewayHttpClient = new SimpleHttpClient();
        devPortalRestApiManager = new DevPortalRestApiManager(gatewayHttpClient, amHost, username, password);
    }

    @AfterClass
    public void tearDown() {
        httpServer.stop(0);
    }

    @Test
    public void createApplicationTest() {
        DevPortalApplication devPortalApplication = new DevPortalApplication(
                null,
                applicationName,
                applicationThrottlingPolicy,
                applicationDescription,
                applicationTokenType,
                null,
                null,
                null
        );

        DevPortalApplication createdApplication = devPortalRestApiManager.createApplication(devPortalApplication);

        Assert.assertNotNull(createdApplication.getApplicationId());

        applicationId = createdApplication.getApplicationId();
    }

    @Test(dependsOnMethods = "createApplicationTest")
    public void retrieveApplicationTest() {
        DevPortalApplication devPortalApplication = devPortalRestApiManager.retrieveApplication(applicationId);

        Assert.assertNotNull(devPortalApplication);
        Assert.assertEquals(devPortalApplication.getApplicationId(), applicationId);
        Assert.assertEquals(devPortalApplication.getName(), applicationName);
        Assert.assertEquals(devPortalApplication.getThrottlingPolicy(), applicationThrottlingPolicy);
        Assert.assertEquals(devPortalApplication.getDescription(), applicationDescription);
        Assert.assertEquals(devPortalApplication.getTokenType(), applicationTokenType);
    }

    @Test(dependsOnMethods = "retrieveApplicationTest")
    public void searchApplicationsByNameTest() {
        DevPortalApplication[] devPortalApplications =
                devPortalRestApiManager
                        .searchApplicationsByName(applicationName)
                        .toArray(new DevPortalApplication[0]);

        Assert.assertNotNull(devPortalApplications);
        Assert.assertTrue(devPortalApplications.length > 0);
        Assert.assertEquals(devPortalApplications[0].getApplicationId(), applicationId);
        Assert.assertEquals(devPortalApplications[0].getName(), applicationName);
        Assert.assertEquals(devPortalApplications[0].getThrottlingPolicy(), applicationThrottlingPolicy);
        Assert.assertEquals(devPortalApplications[0].getDescription(), applicationDescription);
        Assert.assertEquals(devPortalApplications[0].getTokenType(), applicationTokenType);
    }

    @Test
    public void searchApplicationsByTagTest() {
        List<String> apiIds = devPortalRestApiManager.searchAPIsByTag(tag);
        Assert.assertNotNull(apiIds);
        Assert.assertFalse(apiIds.isEmpty());
        String apiId = apiIds.get(0);
        Assert.assertEquals(apiId, expectedApiId);
    }

    @Test(dependsOnMethods = "retrieveApplicationTest")
    public void mapApplicationKeysTest() {
        keyMappingId = devPortalRestApiManager.mapApplicationKeys(
                applicationId,
                consumerKey,
                consumerSecret,
                "IS7KM",
                false
        );

        Assert.assertNotNull(keyMappingId);
    }

    @Test(dependsOnMethods = {"retrieveApplicationTest", "mapApplicationKeysTest"})
    public void subscribeToAPIsTest() {
        String[] apiIds = new String[]{expectedApiId};
        List<String> subscriptionIds =
                devPortalRestApiManager.
                        subscribeToAPIs(applicationId, new String[]{expectedApiId});
        Assert.assertFalse(false);
        subscriptionId = subscriptionIds.get(0);
        Assert.assertNotNull(subscriptionIds);
    }

    @Test(dependsOnMethods = "subscribeToAPIsTest")
    public void unsubscribeToAPIsTest() {
        devPortalRestApiManager.unsubscribeToAPI(subscriptionId);
    }

    @Test(dependsOnMethods = "retrieveApplicationTest")
    public void updateApplicationTest() {
        String updatedApplicationName = applicationName + "updated";
        String updatedApplicationDescription = applicationDescription + "updated";

        DevPortalApplication devPortalApplication = new DevPortalApplication(
                applicationId,
                updatedApplicationName,
                applicationThrottlingPolicy,
                updatedApplicationDescription,
                applicationTokenType,
                null,
                null,
                null
        );

        DevPortalApplication updatedApplication = devPortalRestApiManager.updateApplication(devPortalApplication);

        Assert.assertNotNull(updatedApplication);
        Assert.assertEquals(updatedApplication.getApplicationId(), applicationId);
        Assert.assertEquals(updatedApplication.getName(), updatedApplicationName);
        Assert.assertEquals(updatedApplication.getDescription(), updatedApplicationDescription);
    }

    @Test(dependsOnMethods = {"updateApplicationTest", "unsubscribeToAPIsTest"})
    public void deleteApplicationTest() {
        devPortalRestApiManager.deleteApplication(applicationId);
    }
}
