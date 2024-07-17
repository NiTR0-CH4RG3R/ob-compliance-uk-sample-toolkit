package com.wso2.openbanking.uk.gateway.handler.core;

import com.wso2.openbanking.uk.gateway.internal.GatewayDataHolder;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;
import org.wso2.carbon.apimgt.common.gateway.extensionlistener.ExtensionListener;

/**
 * This class is the implementation of the ExtensionListener interface for the Open Banking API.
 */
public class OpenBankingExtensionListenerImpl implements ExtensionListener {
    @Override
    public ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {
        return GatewayDataHolder
                .getInstance()
                .getHandlerChain()
                .handlePreProcessRequest(requestContextDTO);
    }

    @Override
    public ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
        return GatewayDataHolder
                .getInstance()
                .getHandlerChain()
                .handlePostProcessRequest(requestContextDTO);
    }

    @Override
    public ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
        return GatewayDataHolder
                .getInstance()
                .getHandlerChain()
                .handlePreProcessResponse(responseContextDTO);
    }

    @Override
    public ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
        return GatewayDataHolder
                .getInstance()
                .getHandlerChain()
                .handlePostProcessResponse(responseContextDTO);
    }

    @Override
    public String getType() {
        return "";
    }
}
