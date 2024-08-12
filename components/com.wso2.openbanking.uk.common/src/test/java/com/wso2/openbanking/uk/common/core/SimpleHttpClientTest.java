package com.wso2.openbanking.uk.common.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.exception.SimpleHttpClientRuntimeException;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class SimpleHttpClientTest {
    InetSocketAddress address = new InetSocketAddress(8000);
    HttpServer httpServer = null;

    @BeforeClass
    public void setUp() {
        try {
            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpHandler handler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "<?xml version=\"1.0\"?>\n<resource id=\"1234\" name=\"test\" />\n".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/1234.xml", handler);
        httpServer.start();
    }

    @AfterClass
    public void tearDown() {
        httpServer.stop(0);
    }

    @Test
    public void testSendGet() {
        SimpleHttpClient client = new SimpleHttpClient();
        SimpleHttpRequest request = new SimpleHttpRequest(
                HttpMethod.GET,
                "http://localhost:8000/1234.xml",
                null,
                null
        );
        SimpleHttpResponse response = client.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody(),
                "<?xml version=\"1.0\"?>\n<resource id=\"1234\" name=\"test\" />\n");
    }

    @Test
    public void testSendPost() {
        SimpleHttpClient client = new SimpleHttpClient();
        SimpleHttpRequest request = new SimpleHttpRequest(
                HttpMethod.POST,
                "http://localhost:8000/1234.xml",
                "<resource id=\"1234\" name=\"test\" />",
                new HashMap<String, String>() { {
                    put("Content-Type", "application/xml");
                } }
        );
        SimpleHttpResponse response = client.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody(),
                "<?xml version=\"1.0\"?>\n<resource id=\"1234\" name=\"test\" />\n");
    }

    @Test
    public void testSendPut() {
        SimpleHttpClient client = new SimpleHttpClient();
        SimpleHttpRequest request = new SimpleHttpRequest(
                HttpMethod.PUT,
                "http://localhost:8000/1234.xml",
                null,
                null
        );
        SimpleHttpResponse response = client.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody(),
                "<?xml version=\"1.0\"?>\n<resource id=\"1234\" name=\"test\" />\n");
    }

    @Test
    public void testSendDelete() {
        SimpleHttpClient client = new SimpleHttpClient();
        SimpleHttpRequest request = new SimpleHttpRequest(
                HttpMethod.DELETE,
                "http://localhost:8000/1234.xml",
                null,
                null
        );
        SimpleHttpResponse response = client.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testSendInvalidURL() {
        SimpleHttpClient client = new SimpleHttpClient();
        SimpleHttpRequest request = new SimpleHttpRequest(
                HttpMethod.GET,
                "http://localhost/1232344.xml",
                null,
                null
        );
        try {
            client.send(request);
            Assert.fail("Expected SimpleHttpClientRuntimeException");
        } catch (SimpleHttpClientRuntimeException e) {
            Assert.assertEquals(e.getMessage(), "Error sending request");
        }
    }
}
