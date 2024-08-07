package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.constants.HttpMethod;
import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests the SimpleAbstractHttpClient implementation.
 */
public class SimpleAbstractHttpClientTest {
    SimpleHttpClient gatewayHttpClient = new SimpleHttpClient();

    /**
     * Tests the send method of the SimpleHttpClient.
     */
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
