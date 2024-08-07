package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;

/**
 * This interface provides the contract for a simple abstract http client. The client should be able to send a
 * SimpleHttpRequest and return a SimpleHttpResponse.
 */
public interface SimpleAbstractHttpClient {
    /**
     * Sends a SimpleHttpRequest and returns a SimpleHttpResponse.
     *
     * @param request The SimpleHttpRequest to send.
     * @return The SimpleHttpResponse.
     */
    SimpleHttpResponse send(SimpleHttpRequest request);
}
