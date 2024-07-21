package com.wso2.openbanking.uk.gateway.common.interfaces;

import com.wso2.openbanking.uk.gateway.common.constants.GatewayURL;

public interface GatewayURLProvider {
    String getGatewayURL(GatewayURL endpoint);
}
