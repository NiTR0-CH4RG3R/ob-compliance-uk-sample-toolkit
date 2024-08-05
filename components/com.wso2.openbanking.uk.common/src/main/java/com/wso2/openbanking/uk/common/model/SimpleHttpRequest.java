package com.wso2.openbanking.uk.common.model;

import com.wso2.openbanking.uk.common.constants.HttpMethod;

import java.util.Map;

public class SimpleHttpRequest {
    private final HttpMethod method;
    private final String url;
    private final String body;
    private final Map<String, String> headers;

    public SimpleHttpRequest(HttpMethod method, String url, String body, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.headers = headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}