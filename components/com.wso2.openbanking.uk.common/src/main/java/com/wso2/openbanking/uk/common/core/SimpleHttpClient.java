package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.exception.GatewayHttpClientRuntimeException;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import com.wso2.openbanking.uk.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class provides a simple http client implementation. It sends a SimpleHttpRequest and returns a
 * SimpleHttpResponse. It uses the java.net.http.HttpClient to send the request.
 */
public class SimpleHttpClient implements SimpleAbstractHttpClient {
    private static final Log log = LogFactory.getLog(SimpleHttpClient.class);
    private final HttpClient client;

    public SimpleHttpClient() {
        client = HttpClient.newHttpClient();
    }

    /**
     * Sends a SimpleHttpRequest and returns a SimpleHttpResponse.
     *
     * @param request The SimpleHttpRequest to send.
     * @return The SimpleHttpResponse.
     * @throws GatewayHttpClientRuntimeException If an error occurs while sending the request.
     */
    @Override
    public SimpleHttpResponse send(SimpleHttpRequest request) throws GatewayHttpClientRuntimeException {
        HttpRequest.Builder requestBuilder = null;

        try {
            requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(request.getUrl()));
        } catch (IllegalArgumentException | NullPointerException e) {
            String message = "Invalid URL: " + request.getUrl();
            throw new GatewayHttpClientRuntimeException(StringUtil.sanitizeString(message), e);
        }

        // Set headers
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(requestBuilder::header);
        }

        // Set the method and body
        try {
            if (request.getBody() != null) {
                requestBuilder = requestBuilder.method(
                        request.getMethod().name(),
                        HttpRequest.BodyPublishers.ofString(request.getBody())
                );
            } else {
                requestBuilder = requestBuilder.method(
                        request.getMethod().name(),
                        HttpRequest.BodyPublishers.noBody()
                );
            }
        } catch (IllegalArgumentException e) {
            String message = "Invalid method: " + request.getMethod();
            throw new GatewayHttpClientRuntimeException(StringUtil.sanitizeString(message), e);
        }

        // Build the request
        HttpRequest httpRequest = requestBuilder.build();

        // Send the request
        HttpResponse<String> response = null;
        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new GatewayHttpClientRuntimeException("Error sending request", e);
        }

        return new SimpleHttpResponse(response.statusCode(), response.body());
    }
}
