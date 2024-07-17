package com.wso2.openbanking.uk.gateway.internal;

import com.wso2.openbanking.uk.gateway.handler.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.handler.dcr.DCRHandler;

/**
 * This class holds the data generated at runtime required for the gateway component.
 */
public class GatewayDataHolder {
    private static volatile GatewayDataHolder instance = null;

    public static GatewayDataHolder getInstance() {
        if (instance == null) {
            synchronized (GatewayDataHolder.class) {
                if (instance == null) {
                    instance = new GatewayDataHolder();
                }
            }
        }
        return instance;
    }

    private OpenBankingAPIHandler handlerChain = null;

    private GatewayDataHolder() {
        constructHandlerChain();
    }

    public OpenBankingAPIHandler getHandlerChain() {
        return handlerChain;
    }

    private void constructHandlerChain() {
        handlerChain = new OpenBankingAPIHandler();
        handlerChain.setNextHandler(new DCRHandler());
    }
}
