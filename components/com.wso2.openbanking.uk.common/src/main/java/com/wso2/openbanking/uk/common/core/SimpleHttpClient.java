package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.exception.GatewayHttpClientRuntimeException;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class provides a simple http client implementation. It sends a SimpleHttpRequest and returns a
 * SimpleHttpResponse. It uses the Apache HttpClient to send the request.
 */
public class SimpleHttpClient implements SimpleAbstractHttpClient {
    private static final Log log = LogFactory.getLog(SimpleHttpClient.class);
    private final CloseableHttpClient client;

    public SimpleHttpClient() {
        client = HttpClients.createDefault();
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
        HttpRequestBase httpRequest = createHttpRequest(request);

        // Set headers
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(httpRequest::addHeader);
        }

        // Send the request
        try (CloseableHttpResponse response = client.execute(httpRequest)) {
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";
            return new SimpleHttpResponse(response.getStatusLine().getStatusCode(), responseBody);
        } catch (IOException e) {
            throw new GatewayHttpClientRuntimeException("Error sending request", e);
        }
    }

    private HttpRequestBase createHttpRequest(SimpleHttpRequest request) throws GatewayHttpClientRuntimeException {
        try {
            URI uri = new URIBuilder(request.getUrl()).build();
            switch (request.getMethod()) {
                case GET:
                    return new HttpGet(uri);
                case POST:
                    HttpPost postRequest = new HttpPost(uri);
                    if (request.getBody() != null) {
                        postRequest.setEntity(new StringEntity(request.getBody(), "UTF-8"));
                    }
                    return postRequest;
                case PUT:
                    HttpPut putRequest = new HttpPut(uri);
                    if (request.getBody() != null) {
                        putRequest.setEntity(new StringEntity(request.getBody(), "UTF-8"));
                    }
                    return putRequest;
                case DELETE:
                    return new HttpDelete(uri);
                default:
                    throw new GatewayHttpClientRuntimeException("Unsupported HTTP method: " + request.getMethod());
            }
        } catch (URISyntaxException e) {
            throw new GatewayHttpClientRuntimeException("Invalid URL syntax: " + request.getUrl(), e);
        }
    }
}
