package com.wso2.openbanking.uk.gateway.apimapplication.core;

import com.wso2.openbanking.uk.gateway.apimapplication.interfaces.APIMApplicationConfigProvider;

public class APIMApplication {
    private final APIMApplicationConfigProvider configProvider;

    public APIMApplication(APIMApplicationConfigProvider configProvider) {
        this.configProvider = configProvider;
    }
}
