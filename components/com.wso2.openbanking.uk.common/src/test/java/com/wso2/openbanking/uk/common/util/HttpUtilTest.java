package com.wso2.openbanking.uk.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpUtilTest {
    @Test
    public void testGenerateBasicAuthHeader() {
        String username = "admin";
        String password = "admin";

        String actual = HttpUtil.generateBasicAuthHeader(username, password);
        String expected = "Basic YWRtaW46YWRtaW4=";

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testConvertToXWWWFormUrlEncoded() {
        Map<String, String> data = new HashMap<>(){{
            put("key1", "value1");
            put("key2", "value2");
        }};

        String actual = HttpUtil.convertToXWWWFormUrlEncoded(data);
        String expected = "key1=value1&key2=value2";

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testConcatParamsToUrl() {
        String url = "http://localhost:8080";
        Map<String, String> params = new HashMap<>(){{
            put("key1", "value1");
            put("key2", "value2");
        }};

        String actual = HttpUtil.concatParamsToUrl(url, params);
        String expected = "http://localhost:8080?key1=value1&key2=value2";

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testGenerateBearerAuthHeader() {
        String token = "token";
        String expected = "Bearer token";
        String actual = HttpUtil.generateBearerAuthHeader(token);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testExtractPathVariableSentAsLastSegment() {
        String resource = "http://localhost:8080/resource";
        String expected = "resource";
        String actual = HttpUtil.extractPathVariableSentAsLastSegment(resource);
        Assert.assertEquals(actual, expected);
    }
}
