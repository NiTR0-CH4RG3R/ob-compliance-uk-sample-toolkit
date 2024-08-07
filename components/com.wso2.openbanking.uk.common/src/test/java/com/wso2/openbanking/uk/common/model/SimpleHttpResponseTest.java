package com.wso2.openbanking.uk.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class SimpleHttpResponseTest {
    // Test case for SimpleHttpResponse
    @Test
    public void testSimpleHttpResponse() {
        int statusCode = 200;
        String body = "body";
        Map<String, String> headers = new HashMap<>() {{
            put("header1", "value1");
            put("header2", "value2");
        }};

        SimpleHttpResponse response = new SimpleHttpResponse(statusCode, body);

        Assert.assertEquals(response.getStatusCode(), statusCode);
        Assert.assertEquals(response.getBody(), body);
    }
}
