package com.wso2.openbanking.uk.gateway.impl;

import com.wso2.openbanking.uk.gateway.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.impl.handler.DCRHandler;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;
import org.wso2.carbon.apimgt.common.gateway.extensionlistener.ExtensionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of the ExtensionListener interface for the Open Banking API.
 */
public class OpenBankingExtensionListenerImpl implements ExtensionListener {

    private List<OpenBankingAPIHandler> handlerChain = null;

    public OpenBankingExtensionListenerImpl() {
        constructHandlerChain();
    }

    private void constructHandlerChain() {
        handlerChain = new ArrayList<OpenBankingAPIHandler>();
        handlerChain.add(new DCRHandler());
    }

    @Override
    public ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {
        for (OpenBankingAPIHandler handler : handlerChain) {
            if (handler.canProcess(requestContextDTO.getMsgInfo(), requestContextDTO.getApiRequestInfo())) {
                return handler.preProcessRequest(requestContextDTO);
            }
        }
        return null;
    }

    @Override
    public ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
        for (OpenBankingAPIHandler handler : handlerChain) {
            if (handler.canProcess(requestContextDTO.getMsgInfo(), requestContextDTO.getApiRequestInfo())) {
                return handler.postProcessRequest(requestContextDTO);
            }
        }
        return null;
    }

    @Override
    public ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
        for (OpenBankingAPIHandler handler : handlerChain) {
            if (handler.canProcess(responseContextDTO.getMsgInfo(), responseContextDTO.getApiRequestInfo())) {
                return handler.preProcessResponse(responseContextDTO);
            }
        }
        return null;
    }

    @Override
    public ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
        for (OpenBankingAPIHandler handler : handlerChain) {
            if (handler.canProcess(responseContextDTO.getMsgInfo(), responseContextDTO.getApiRequestInfo())) {
                return handler.postProcessResponse(responseContextDTO);
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return "";
    }
}
