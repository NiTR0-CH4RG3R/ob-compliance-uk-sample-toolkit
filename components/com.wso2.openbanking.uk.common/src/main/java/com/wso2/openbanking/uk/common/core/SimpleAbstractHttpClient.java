package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;

public interface SimpleAbstractHttpClient {
    SimpleHttpResponse send(SimpleHttpRequest request);
}
