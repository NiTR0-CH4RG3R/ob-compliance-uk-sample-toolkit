package com.wso2.openbanking.uk.gateway.handler.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;

/**
 * This class is the base class for all the Open Banking API handler nodes in the handler chain. Any new handler that
 * need to be added must extend this class and override the canHandle() and 4 request and response processing methods.
 * If the canHandle() method returns true, the request/response will be passed to the corresponding processing method.
 * If the canHandle() method returns false, the request/response will be passed to the next handler in the chain. Only
 * one handler in the chain will process the request/response.
 */
public class OpenBankingAPIHandler {
    private static final Log log = LogFactory.getLog(OpenBankingAPIHandler.class);

    private OpenBankingAPIHandler nextHandler = null;

    public OpenBankingAPIHandler setNextHandler(OpenBankingAPIHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public ExtensionResponseDTO handlePreProcessRequest(RequestContextDTO requestContextDTO) {
        try {
            if (canProcess(requestContextDTO.getMsgInfo(), requestContextDTO.getApiRequestInfo())) {
                return preProcessRequest(requestContextDTO);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the request", e);
        }

        if (nextHandler != null) {
            return nextHandler.handlePreProcessRequest(requestContextDTO);
        }

        return null;
    }

    public ExtensionResponseDTO handlePostProcessRequest(RequestContextDTO requestContextDTO) {
        try {
            if (canProcess(requestContextDTO.getMsgInfo(), requestContextDTO.getApiRequestInfo())) {
                return postProcessRequest(requestContextDTO);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the request", e);
        }

        if (nextHandler != null) {
            return nextHandler.handlePostProcessRequest(requestContextDTO);
        }

        return null;
    }

    public ExtensionResponseDTO handlePreProcessResponse(ResponseContextDTO responseContextDTO) {
        try {
            if (canProcess(responseContextDTO.getMsgInfo(), responseContextDTO.getApiRequestInfo())) {
                return preProcessResponse(responseContextDTO);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the request", e);
        }

        if (nextHandler != null) {
            return nextHandler.handlePreProcessResponse(responseContextDTO);
        }

        return null;
    }

    public ExtensionResponseDTO handlePostProcessResponse(ResponseContextDTO responseContextDTO) {
        try {
            if (canProcess(responseContextDTO.getMsgInfo(), responseContextDTO.getApiRequestInfo())) {
                return postProcessResponse(responseContextDTO);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the request", e);
        }

        if (nextHandler != null) {
            return nextHandler.handlePostProcessResponse(responseContextDTO);
        }

        return null;
    }

    protected boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
        return false;
    }

    protected ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO) {
        return null;
    }

    protected ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO) {
        return null;
    }

    protected ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO) {
        return null;
    }

    protected ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO) {
        return null;
    }
}
