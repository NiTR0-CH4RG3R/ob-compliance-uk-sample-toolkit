package com.wso2.openbanking.uk.gateway.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the DevPortalApplication.
 */
public class DevPortalApplicationTest {

    String expectedApplicationName = "TestApplication";
    String expectedApplicationId = "TestApplicationId";
    String expectedThrottlingPolicy = "TestThrottlingPolicy";
    String expectedDescription = "TestDescription";
    String expectedTokenType = "TestTokenType";
    String[] expectedGroups = {"TestGroup1", "TestGroup2"};
    Map<String, String> expectedAttributes = new HashMap<String, String>() { {
        put("TestAttribute1", "TestValue1");
        put("TestAttribute2", "TestValue2");
    } };
    String[] expectedScopes = {"TestScope1", "TestScope2"};

    DevPortalApplication devPortalApplication = new DevPortalApplication(
            expectedApplicationId,
            expectedApplicationName,
            expectedThrottlingPolicy,
            expectedDescription,
            expectedTokenType,
            expectedGroups,
            expectedAttributes,
            expectedScopes
    );

    @Test
    public void testDevPortalApplication() {
        Assert.assertEquals(expectedApplicationName, devPortalApplication.getName());
        Assert.assertEquals(expectedApplicationId, devPortalApplication.getApplicationId());
        Assert.assertEquals(expectedThrottlingPolicy, devPortalApplication.getThrottlingPolicy());
        Assert.assertEquals(expectedDescription, devPortalApplication.getDescription());
        Assert.assertEquals(expectedTokenType, devPortalApplication.getTokenType());
        Assert.assertTrue(Arrays.equals(devPortalApplication.getGroups(), expectedGroups));
        Assert.assertEquals(expectedAttributes, devPortalApplication.getAttributes());
        Assert.assertTrue(Arrays.equals(devPortalApplication.getSubscriptionScopes(), expectedScopes));
    }
}
