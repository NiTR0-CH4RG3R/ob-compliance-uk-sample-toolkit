package com.wso2.openbanking.uk.gateway.core.handler.dcr;

import com.wso2.openbanking.uk.gateway.handler.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.handler.exception.OpenBankingAPIHandlerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;
import java.util.Locale;

/**
 * This class is the handler for the Dynamic Client Registration (DCR) API.
 */
public class DCRHandler extends OpenBankingAPIHandler {
    private static final Log log = LogFactory.getLog(DCRHandler.class);

    @Override
    protected boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO)
            throws OpenBankingAPIHandlerException {
        return msgInfoDTO.getResource().toLowerCase(Locale.getDefault()).contains("/register");
    }

    @Override
    protected ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        log.debug("DCRHandler preProcessRequest");
        return null;
    }

    @Override
    protected ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        log.debug("DCRHandler postProcessRequest");
        return null;
    }

    @Override
    protected ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {
        log.debug("DCRHandler preProcessResponse");
        return null;
    }

    @Override
    protected ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {
        log.debug("DCRHandler postProcessResponse");
        return null;
    }
}
