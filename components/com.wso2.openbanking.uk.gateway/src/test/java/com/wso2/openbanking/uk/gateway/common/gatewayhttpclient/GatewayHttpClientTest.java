package com.wso2.openbanking.uk.gateway.common.gatewayhttpclient;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GatewayHttpClientTest {

    GatewayHttpClient gatewayHttpClient = new GatewayHttpClient();

    @Test
    public void testSend() {
        GatewayHttpRequest request = new GatewayHttpRequest(
                "GET",
                "http://www.example.com/",
                null, null
        );
        GatewayHttpResponse response = gatewayHttpClient.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
