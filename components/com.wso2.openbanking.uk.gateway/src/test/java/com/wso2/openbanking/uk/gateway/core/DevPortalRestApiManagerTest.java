package com.wso2.openbanking.uk.gateway.core;

import com.wso2.openbanking.uk.common.core.SimpleHttpClient;
import com.wso2.openbanking.uk.gateway.model.DevPortalApplication;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

        SimpleHttpClient gatewayHttpClient = new SimpleHttpClient();
        devPortalRestApiManager = new DevPortalRestApiManager(gatewayHttpClient, amHost, username, password);
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
