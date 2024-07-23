package com.wso2.openbanking.uk.gateway.core.impl;

import com.wso2.openbanking.uk.gateway.core.handler.dcr.DCRHandler;
import com.wso2.openbanking.uk.gateway.handler.core.OpenBankingAPIHandler;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;
import org.wso2.carbon.apimgt.common.gateway.extensionlistener.ExtensionListener;

/**
 * This class is the implementation of the ExtensionListener interface for the Open Banking API.
 */
public class OpenBankingExtensionListenerImpl implements ExtensionListener {

    private OpenBankingAPIHandler handlerChain = null;

    public OpenBankingExtensionListenerImpl() {
        constructHandlerChain();
    }

    private void constructHandlerChain() {
        handlerChain = new OpenBankingAPIHandler();
        handlerChain
                .setNextHandler(new DCRHandler());
    }

    @Override
    public ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {
        return handlerChain.handlePreProcessRequest(requestContextDTO);
    }

    @Override
    public ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
        return handlerChain.handlePostProcessRequest(requestContextDTO);
    }

    @Override
    public ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
        return handlerChain.handlePreProcessResponse(responseContextDTO);
    }

    @Override
    public ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
        return handlerChain.handlePostProcessResponse(responseContextDTO);
    }

    @Override
    public String getType() {
        return "";
    }
}
