package com.wso2.openbanking.uk.gateway.core.handler.dcr.apimapplication;

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

public class APIMApplicationManagerTest {

    private APIMApplicationManager apimApplicationManager;

    private String applicationId = null;
    private final String applicationName = "TestApplication10";
    private final String applicationThrottlingPolicy = "Unlimited";
    private final String applicationDescription = "description";
    private final String applicationTokenType = "JWT";

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
        apimApplicationManager = new APIMApplicationManager(gatewayHttpClient, amHost, username, password);
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

        APIMApplication createdApplication = apimApplicationManager.createApplication(apimApplication);

        Assert.assertNotNull(createdApplication.getApplicationId());

        applicationId = createdApplication.getApplicationId();
    }

    @Test(dependsOnMethods = "createApplicationTest")
    public void retrieveApplicationTest() {
        APIMApplication apimApplication = apimApplicationManager.retrieveApplication(applicationId);

        Assert.assertNotNull(apimApplication);
        Assert.assertEquals(apimApplication.getApplicationId(), applicationId);
        Assert.assertEquals(apimApplication.getName(), applicationName);
        Assert.assertEquals(apimApplication.getThrottlingPolicy(), applicationThrottlingPolicy);
        Assert.assertEquals(apimApplication.getDescription(), applicationDescription);
        Assert.assertEquals(apimApplication.getTokenType(), applicationTokenType);
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

        APIMApplication updatedApplication = apimApplicationManager.updateApplication(apimApplication);

        Assert.assertNotNull(updatedApplication);
        Assert.assertEquals(updatedApplication.getApplicationId(), applicationId);
        Assert.assertEquals(updatedApplication.getName(), updatedApplicationName);
        Assert.assertEquals(updatedApplication.getDescription(), updatedApplicationDescription);
    }

    @Test(dependsOnMethods = "updateApplicationTest")
    public void deleteApplicationTest() {
        apimApplicationManager.deleteApplication(applicationId);
    }
}
