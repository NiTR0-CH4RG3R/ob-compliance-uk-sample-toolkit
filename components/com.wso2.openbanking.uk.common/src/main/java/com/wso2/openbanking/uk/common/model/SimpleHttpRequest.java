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
