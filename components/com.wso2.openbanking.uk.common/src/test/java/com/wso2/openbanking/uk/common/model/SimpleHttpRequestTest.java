package com.wso2.openbanking.uk.common.model;

import com.wso2.openbanking.uk.common.constants.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class SimpleHttpRequestTest {
    // Test case for SimpleHttpRequest
    @Test
    public void testSimpleHttpRequest() {
        HttpMethod httpMethod = HttpMethod.GET;
        String url = "http://www.example.com/";
        String body = "body";
        Map<String, String> headers = new HashMap<>() {{
            put("header1", "value1");
            put("header2", "value2");
        }};

        SimpleHttpRequest request = new SimpleHttpRequest(httpMethod, url, body, headers);

        Assert.assertEquals(request.getMethod(), httpMethod);
        Assert.assertEquals(request.getUrl(), url);
        Assert.assertEquals(request.getBody(), body);
        Assert.assertEquals(request.getHeaders(), headers);
    }
}
