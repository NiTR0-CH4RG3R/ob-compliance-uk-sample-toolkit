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

package com.wso2.openbanking.uk.gateway.core;

import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;

/**
 * Interface for Open Banking API handlers. This interface should be implemented by the classes that handle Open Banking
 * API requests and responses. If a class implements this interface, need to be processed for any API flow it must
 * return true for the canProcess() method.
 */
public interface OpenBankingAPIHandler {

    /**
     * Returns whether the handler can process the given request and response.
     *
     * @param msgInfoDTO MsgInfoDTO object received from the RequestContextDTO or ResponseContextDTO.
     * @param apiRequestInfoDTO APIRequestInfoDTO object received from the RequestContextDTO.
     * @return True if the handler should process the request and response, false otherwise.
     */
    boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO);

    /**
     * This method is called by the OpenBankingExtensionListenerImpl in its preProcessRequest() method. All the
     * parameters passed to this method are the same as the ones passed to the preProcessRequest() method of the
     * ExtensionListener interface. This method will be called before the authentication of the request.
     *
     * @param requestContextDTO The RequestContextDTO object received from the ExtensionListener.
     * @return The ExtensionResponseDTO object that contains the response of the handler.
     */
    ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO);

    /**
     * This method is called by the OpenBankingExtensionListenerImpl in its postProcessRequest() method. All the
     * parameters passed to this method are the same as the ones passed to the postProcessRequest() method of the
     * ExtensionListener interface. This method will be called after the authentication of the request before it
     * is sent to the backend.
     *
     * @param requestContextDTO The RequestContextDTO object received from the ExtensionListener.
     * @return The ExtensionResponseDTO object that contains the response of the handler.
     */
    ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO);

    /**
     * This method is called by the OpenBankingExtensionListenerImpl in its preProcessResponse() method. All the
     * parameters passed to this method are the same as the ones passed to the preProcessResponse() method of the
     * ExtensionListener interface. This method will be called after the response is received from the backend.
     *
     * @param responseContextDTO The ResponseContextDTO object received from the ExtensionListener.
     * @return The ExtensionResponseDTO object that contains the response of the handler.
     */
    ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO);

    /**
     * This method is called by the OpenBankingExtensionListenerImpl in its postProcessResponse() method. All the
     * parameters passed to this method are the same as the ones passed to the postProcessResponse() method of the
     * ExtensionListener interface. This method will be called after the preProcessResponse() method, if the
     * preProcessResponse() method returns a success response.
     *
     * @param responseContextDTO The ResponseContextDTO object received from the ExtensionListener.
     * @return The ExtensionResponseDTO object that contains the response of the handler.
     */
    ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO);
}
