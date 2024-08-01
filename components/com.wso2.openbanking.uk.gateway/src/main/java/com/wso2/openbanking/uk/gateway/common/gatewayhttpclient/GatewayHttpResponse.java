package com.wso2.openbanking.uk.gateway.common.gatewayhttpclient;

public class GatewayHttpResponse {
    private final int statusCode;
    private final String body;

    public GatewayHttpResponse(int statusCode, String body) {
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