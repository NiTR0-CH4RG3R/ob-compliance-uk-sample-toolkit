package com.wso2.openbanking.uk.gateway.core;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests the OpenBankingAPIHandler.
 */
public class OpenBankingAPIHandlerTest {
    @Test
    public void testHandlerOrder() {
        String handler1Identifier = "handler1";
        String handler2Identifier = "handler2";

        List<String> handlerOrder = new ArrayList<String>();

        OpenBankingAPIHandler handler1 = new OpenBankingAPIHandler() {
            @Override
            public boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
                return true;
            }

            @Override
            public ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {
                handlerOrder.add(handler1Identifier);
                return null;
            }

            @Override
            public ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
                return null;
            }

            @Override
            public ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
                return null;
            }

            @Override
            public ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
                return null;
            }
        };

        OpenBankingAPIHandler handler2 = new OpenBankingAPIHandler() {
            @Override
            public boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
                return true;
            }

            @Override
            public ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {
                handlerOrder.add(handler2Identifier);
                return null;
            }

            @Override
            public ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
                return null;
            }

            @Override
            public ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
                return null;
            }

            @Override
            public ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
                return null;
            }
        };

        List<OpenBankingAPIHandler> handlerChain = new ArrayList<OpenBankingAPIHandler>();

        handlerChain.add(handler1);
        handlerChain.add(handler2);

        for (OpenBankingAPIHandler handler : handlerChain) {
            if (handler.canProcess(new MsgInfoDTO(), new APIRequestInfoDTO())) {
                handler.preProcessRequest(new RequestContextDTO());
            }
        }

        Assert.assertEquals(handlerOrder.get(0), handler1Identifier);
        Assert.assertEquals(handlerOrder.get(1), handler2Identifier);
    }
}
