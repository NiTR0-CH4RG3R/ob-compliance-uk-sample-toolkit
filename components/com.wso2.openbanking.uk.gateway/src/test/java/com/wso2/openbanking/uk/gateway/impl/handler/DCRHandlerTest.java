package com.wso2.openbanking.uk.gateway.impl.handler;

import com.wso2.openbanking.uk.gateway.exception.OpenBankingAPIHandlerException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.extensionlistener.PayloadHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DCRHandlerTest {
    DCRHandler dcrHandler = null;

    @BeforeClass
    public void setUp() {
        dcrHandler = new DCRHandler();
    }

    @Test
    public void testCanProcess() {
        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        boolean canProcess = dcrHandler.canProcess(msgInfoDTO, null);
        Assert.assertTrue(canProcess);
    }

    @Test
    public void testPreProcessRequest() throws OpenBankingAPIHandlerException {
        String modifiedPayload = "";
        String inputPayload = "";

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(new HashMap<>(){{put("Content-Type", "application/json");}});
        msgInfoDTO.setHttpMethod("POST");
        msgInfoDTO.setPayloadHandler(new PayloadHandler() {
            @Override
            public String consumeAsString() throws Exception {
                return inputPayload;
            }

            @Override
            public InputStream consumeAsStream() throws Exception {
                return null;
            }
        });

        RequestContextDTO requestContextDTO = new RequestContextDTO();
        requestContextDTO.setMsgInfo(msgInfoDTO);

        ExtensionResponseDTO extensionResponseDTO =  dcrHandler.preProcessRequest(requestContextDTO);

        Assert.assertNotNull(extensionResponseDTO);
        Assert.assertEquals(extensionResponseDTO.getStatusCode(), 200);
        Assert.assertEquals(extensionResponseDTO.getHeaders().get("Content-Type"), "application/json");
        Assert.assertEquals(extensionResponseDTO.getPayload(), new ByteArrayInputStream(modifiedPayload.getBytes(StandardCharsets.UTF_8)));
    }

}
