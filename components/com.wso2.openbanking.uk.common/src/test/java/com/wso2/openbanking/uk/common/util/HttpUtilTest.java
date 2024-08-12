/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wso2.openbanking.uk.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the HttpUtil implementation.
 */
public class HttpUtilTest {

    /**
     * Tests the generateBasicAuthHeader method of the HttpUtil.
     */
    @Test
    public void testGenerateBasicAuthHeader() {
        String username = "admin";
        String password = "admin";

        String actual = HttpUtil.generateBasicAuthHeader(username, password);
        String expected = "Basic YWRtaW46YWRtaW4=";

        Assert.assertEquals(actual, expected);
    }

    /**
     * Tests the convertToXWWWFormUrlEncoded method of the HttpUtil.
     */
    @Test
    public void testConvertToXWWWFormUrlEncoded() {
        Map<String, String> data = new HashMap<String, String>() { {
            put("key1", "value1");
            put("key2", "value2");
        } };

        String actual = HttpUtil.convertToXWWWFormUrlEncoded(data);
        String expected = "key1=value1&key2=value2";

        Assert.assertEquals(actual, expected);
    }

    /**
     * Tests the concatParamsToUrl method of the HttpUtil.
     */
    @Test
    public void testConcatParamsToUrl() {
        String url = "http://localhost:8080";
        Map<String, String> params = new HashMap<String, String>() { {
            put("key1", "value1");
            put("key2", "value2");
        } };

        String actual = HttpUtil.concatParamsToUrl(url, params);
        String expected = "http://localhost:8080?key1=value1&key2=value2";

        Assert.assertEquals(actual, expected);
    }

    /**
     * Tests the generateBearerAuthHeader method of the HttpUtil.
     */
    @Test
    public void testGenerateBearerAuthHeader() {
        String token = "token";
        String expected = "Bearer token";
        String actual = HttpUtil.generateBearerAuthHeader(token);
        Assert.assertEquals(actual, expected);
    }

    /**
     * Tests the extractPathVariableSentAsLastSegment method of the HttpUtil.
     */
    @Test
    public void testExtractPathVariableSentAsLastSegment() {
        String resource = "http://localhost:8080/resource/";
        String expected = "resource";
        String actual = HttpUtil.extractPathVariableSentAsLastSegment(resource);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testExtractBearerTokenWithValidToken() {
        String validBearerToken = " Bearer   token ";
        String expected = "token";

        String actual = HttpUtil.extractBearerToken(validBearerToken);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testExtractBearerTokenWithInvalidToken() {
        String invalidBearer = "Bearer";

        String actual = HttpUtil.extractBearerToken(invalidBearer);
        Assert.assertNull(actual);

        invalidBearer = "Bearerer token";
        actual = HttpUtil.extractBearerToken(invalidBearer);
        Assert.assertNull(actual);
    }

    @Test
    public void testExtractBearerTokenWithNullToken() {
        String actual = HttpUtil.extractBearerToken(null);
        Assert.assertNull(actual);
    }

    @Test
    public void testExtractBearerTokenWithEmptyToken() {
        String actual = HttpUtil.extractBearerToken("");
        Assert.assertNull(actual);
    }
}
