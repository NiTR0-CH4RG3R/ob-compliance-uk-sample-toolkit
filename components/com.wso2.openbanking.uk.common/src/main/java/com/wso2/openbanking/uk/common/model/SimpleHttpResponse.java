package com.wso2.openbanking.uk.common.model;

public class SimpleHttpResponse {
    private final int statusCode;
    private final String body;

    public SimpleHttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}