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

import com.wso2.openbanking.uk.common.constants.HttpMethod;
import java.util.Map;

/**
 * This class represents a simple http request. It contains the http method, url, body and headers of the request.
 */
public class SimpleHttpRequest {
    private final HttpMethod method;
    private final String url;
    private final String body;
    private final Map<String, String> headers;

    /**
     * Constructs a SimpleHttpRequest object with the given method, url, body and headers.
     *
     * @param method The http method of the request.
     * @param url The url of the request.
     * @param body The body of the request.
     * @param headers The headers of the request.
     */
    public SimpleHttpRequest(HttpMethod method, String url, String body, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.headers = headers;
    }

    /**
     * Returns the http method of the request.
     *
     * @return The http method of the request.
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Returns the url of the request.
     *
     * @return The url of the request.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the body of the request.
     *
     * @return The body of the request.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the headers of the request.
     *
     * @return The headers of the request.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
}
