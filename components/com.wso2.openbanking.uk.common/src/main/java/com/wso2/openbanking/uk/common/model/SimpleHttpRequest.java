package com.wso2.openbanking.uk.common.model;

import java.util.Map;

public class SimpleHttpRequest {
    private final String method;
    private final String url;
    private final String body;
    private final Map<String, String> headers;

    public SimpleHttpRequest(String method, String url, String body, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() {
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