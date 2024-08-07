package com.wso2.openbanking.uk.gateway.core;

import com.wso2.openbanking.uk.gateway.exception.OpenBankingAPIHandlerException;
import graphql.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.*;

public class OpenBankingAPIHandlerTest {

    private OpenBankingAPIHandler openBankingAPIHandler = new OpenBankingAPIHandler();

    private static boolean isPreProcessRequest = false;
    private static boolean isPostProcessRequest = false;
    private static boolean isPreProcessResponse = false;
    private static boolean isPostProcessResponse = false;

    private static class OpenBankingAPIHandlerExtended extends OpenBankingAPIHandler {
        @Override
        public boolean canProcess(
                MsgInfoDTO msgInfoDTO,
                APIRequestInfoDTO apiRequestInfoDTO
        ) {
            return true;
        }

        @Override
        public ExtensionResponseDTO preProcessRequest(
                RequestContextDTO requestContextDTO
        ) throws OpenBankingAPIHandlerException {
            isPreProcessRequest = true;
            return null;
        }

        @Override
        public ExtensionResponseDTO postProcessRequest(
                RequestContextDTO requestContextDTO
        ) throws OpenBankingAPIHandlerException {
            isPostProcessRequest = true;
            return null;
        }

        @Override
        public ExtensionResponseDTO preProcessResponse(
                ResponseContextDTO responseContextDTO
        ) throws OpenBankingAPIHandlerException {
            isPreProcessResponse = true;
            return null;
        }

        @Override
        public ExtensionResponseDTO postProcessResponse(
                ResponseContextDTO responseContextDTO
        ) throws OpenBankingAPIHandlerException {
            isPostProcessResponse = true;
            return null;
        }

    }

    @BeforeClass
    public void setup() {
        openBankingAPIHandler.setNextHandler(new OpenBankingAPIHandlerExtended());
    }

    @Test
    public void testHandlePreProcessRequest() {
        RequestContextDTO requestContextDTO = new RequestContextDTO();
        openBankingAPIHandler.handlePreProcessRequest(requestContextDTO);
        Assert.assertTrue(isPreProcessRequest);
    }

    @Test
    public void testHandlePostProcessRequest() {
        RequestContextDTO requestContextDTO = new RequestContextDTO();
        openBankingAPIHandler.handlePostProcessRequest(requestContextDTO);
        Assert.assertTrue(isPostProcessRequest);
    }

    @Test
    public void testHandlePreProcessResponse() {
        ResponseContextDTO responseContextDTO = new ResponseContextDTO();
        openBankingAPIHandler.handlePreProcessResponse(responseContextDTO);
        Assert.assertTrue(isPreProcessResponse);
    }

    @Test
    public void testHandlePostProcessResponse() {
        ResponseContextDTO responseContextDTO = new ResponseContextDTO();
        openBankingAPIHandler.handlePostProcessResponse(responseContextDTO);
        Assert.assertTrue(isPostProcessResponse);
    }
}
