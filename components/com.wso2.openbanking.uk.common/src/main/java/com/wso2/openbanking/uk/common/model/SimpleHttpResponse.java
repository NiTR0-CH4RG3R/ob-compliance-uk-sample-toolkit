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
