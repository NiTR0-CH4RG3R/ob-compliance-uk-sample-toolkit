package com.wso2.openbanking.uk.gateway.core.handler.dcr.devportal;

import com.wso2.openbanking.uk.gateway.common.gatewayhttpclient.GatewayHttpClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

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


    @BeforeClass
    public void setUp() throws NoSuchAlgorithmException, KeyManagementException {
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

        String amHost = "https://localhost:9443";
        String username = "admin";
        String password = "admin";

        GatewayHttpClient gatewayHttpClient = new GatewayHttpClient();
        devPortalRestApiManager = new DevPortalRestApiManager(gatewayHttpClient, amHost, username, password);
    }

    @Test
    public void createApplicationTest() {
        APIMApplication apimApplication = new APIMApplication(
                null,
                applicationName,
                applicationThrottlingPolicy,
                applicationDescription,
                applicationTokenType,
                null,
                null,
                null
        );

        APIMApplication createdApplication = devPortalRestApiManager.createApplication(apimApplication);

        Assert.assertNotNull(createdApplication.getApplicationId());

        applicationId = createdApplication.getApplicationId();
    }

    @Test(dependsOnMethods = "createApplicationTest")
    public void retrieveApplicationTest() {
        APIMApplication apimApplication = devPortalRestApiManager.retrieveApplication(applicationId);

        Assert.assertNotNull(apimApplication);
        Assert.assertEquals(apimApplication.getApplicationId(), applicationId);
        Assert.assertEquals(apimApplication.getName(), applicationName);
        Assert.assertEquals(apimApplication.getThrottlingPolicy(), applicationThrottlingPolicy);
        Assert.assertEquals(apimApplication.getDescription(), applicationDescription);
        Assert.assertEquals(apimApplication.getTokenType(), applicationTokenType);
    }

    @Test(dependsOnMethods = "retrieveApplicationTest")
    public void searchApplicationsByNameTest() {
        APIMApplication[] apimApplications = devPortalRestApiManager.searchApplicationsByName(applicationName).toArray(new APIMApplication[0]);

        Assert.assertNotNull(apimApplications);
        Assert.assertTrue(apimApplications.length > 0);
        Assert.assertEquals(apimApplications[0].getApplicationId(), applicationId);
        Assert.assertEquals(apimApplications[0].getName(), applicationName);
        Assert.assertEquals(apimApplications[0].getThrottlingPolicy(), applicationThrottlingPolicy);
        Assert.assertEquals(apimApplications[0].getDescription(), applicationDescription);
        Assert.assertEquals(apimApplications[0].getTokenType(), applicationTokenType);
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
                true
        );

        Assert.assertNotNull(keyMappingId);
    }

    @Test(dependsOnMethods = {"retrieveApplicationTest", "mapApplicationKeysTest"})
    public void subscribeToAPIsTest() {
        String[] apiIds = new String[]{expectedApiId};
        List<String> subscriptionIds = devPortalRestApiManager.subscribeToAPIs(applicationId, new String[]{expectedApiId});
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

        APIMApplication apimApplication = new APIMApplication(
                applicationId,
                updatedApplicationName,
                applicationThrottlingPolicy,
                updatedApplicationDescription,
                applicationTokenType,
                null,
                null,
                null
        );

        APIMApplication updatedApplication = devPortalRestApiManager.updateApplication(apimApplication);

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
