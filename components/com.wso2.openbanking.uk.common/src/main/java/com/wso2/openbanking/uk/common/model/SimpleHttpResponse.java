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

/**
 * This class represents a simple http response. It contains the status code and body of the response.
 */
public class SimpleHttpResponse {
    private final int statusCode;
    private final String body;

    /**
     * Constructs a SimpleHttpResponse object with the given status code and body.
     *
     * @param statusCode The status code of the response.
     * @param body The body of the response.
     */
    public SimpleHttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Returns the status code of the response.
     *
     * @return The status code of the response.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the body of the response.
     *
     * @return The body of the response.
     */
    public String getBody() {
        return body;
    }
}
