/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
