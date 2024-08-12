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

package com.wso2.openbanking.uk.common.model;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the SimpleHttpResponse implementation.
 */
public class SimpleHttpResponseTest {

    /**
     * Tests the SimpleHttpResponse constructor.
     */
    @Test
    public void testSimpleHttpResponse() {
        int statusCode = 200;
        String body = "body";
        Map<String, String> headers = new HashMap<String, String>() { {
            put("header1", "value1");
            put("header2", "value2");
        } };

        SimpleHttpResponse response = new SimpleHttpResponse(statusCode, body);

        Assert.assertEquals(response.getStatusCode(), statusCode);
        Assert.assertEquals(response.getBody(), body);
    }
}
