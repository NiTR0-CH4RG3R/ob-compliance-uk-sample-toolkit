package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleAbstractHttpClientTest {
    SimpleHttpClient gatewayHttpClient = new SimpleHttpClient();

    @Test
    public void testSend() {
        SimpleHttpRequest request = new SimpleHttpRequest(
                HttpMethod.GET,
                "http://www.example.com/",
                null, null
        );
        SimpleHttpResponse response = gatewayHttpClient.send(request);
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
