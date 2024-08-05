package com.wso2.openbanking.uk.gateway.common.gatewayhttpclient;

import com.wso2.openbanking.uk.common.core.SimpleHttpClient;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GatewayHttpClientTest {

    SimpleHttpClient gatewayHttpClient = new SimpleHttpClient();

    @Test
    public void testSend() {
        SimpleHttpRequest request = new SimpleHttpRequest(
                "GET",
                "http://www.example.com/",
                null, null
        );
        SimpleHttpResponse response = gatewayHttpClient.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
