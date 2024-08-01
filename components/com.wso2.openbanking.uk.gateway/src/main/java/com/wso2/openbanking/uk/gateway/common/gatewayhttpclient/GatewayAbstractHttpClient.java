package com.wso2.openbanking.uk.gateway.common.gatewayhttpclient;

import java.util.Map;

public interface GatewayAbstractHttpClient {
    GatewayHttpResponse send(GatewayHttpRequest request);
}
