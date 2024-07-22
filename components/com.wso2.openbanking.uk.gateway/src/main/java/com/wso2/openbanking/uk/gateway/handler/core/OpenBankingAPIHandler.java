package com.wso2.openbanking.uk.gateway.handler.core;

import com.wso2.openbanking.uk.gateway.handler.constants.HttpHeader;
import com.wso2.openbanking.uk.gateway.handler.constants.HttpHeaderContentType;
import com.wso2.openbanking.uk.gateway.handler.exception.OpenBankingAPIHandlerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.XML;
import org.wso2.carbon.apimgt.common.gateway.dto.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        if (canProcess(requestContextDTO.getMsgInfo(), requestContextDTO.getApiRequestInfo())) {
            ExtensionResponseDTO responseDTO = null;
            try {
                 responseDTO = preProcessRequest(requestContextDTO);
                 return createContinueExtensionResponseDTOFromExtensionPayload(responseDTO);
            } catch (OpenBankingAPIHandlerException e) {
                log.error("Error occurred in the pre-auth-request-processing step!", e);
                return createErrorExtensionResponseDTO(requestContextDTO.getMsgInfo());
            }
        }

        if (nextHandler != null) {
            return nextHandler.handlePreProcessRequest(requestContextDTO);
        }

        return null;
    }

    public ExtensionResponseDTO handlePostProcessRequest(RequestContextDTO requestContextDTO) {
        if (canProcess(requestContextDTO.getMsgInfo(), requestContextDTO.getApiRequestInfo())) {
            ExtensionResponseDTO responseDTO = null;
            try {
                responseDTO = postProcessRequest(requestContextDTO);
                return createContinueExtensionResponseDTOFromExtensionPayload(responseDTO);
            } catch (OpenBankingAPIHandlerException e) {
                log.error("Error occurred in the post-auth-request-processing step!", e);
                return createErrorExtensionResponseDTO(requestContextDTO.getMsgInfo());
            }
        }

        if (nextHandler != null) {
            return nextHandler.handlePostProcessRequest(requestContextDTO);
        }

        return null;
    }

    public ExtensionResponseDTO handlePreProcessResponse(ResponseContextDTO responseContextDTO) {
        if (canProcess(responseContextDTO.getMsgInfo(), responseContextDTO.getApiRequestInfo())) {
            ExtensionResponseDTO responseDTO = null;
            try {
                responseDTO = preProcessResponse(responseContextDTO);
                return createContinueExtensionResponseDTOFromExtensionPayload(responseDTO);
            } catch (OpenBankingAPIHandlerException e) {
                log.error("Error occurred in the pre-auth-response-processing step!", e);
                return createErrorExtensionResponseDTO(responseContextDTO.getMsgInfo());
            }
        }

        if (nextHandler != null) {
            return nextHandler.handlePreProcessResponse(responseContextDTO);
        }

        return null;
    }

    public ExtensionResponseDTO handlePostProcessResponse(ResponseContextDTO responseContextDTO) {
        if (canProcess(responseContextDTO.getMsgInfo(), responseContextDTO.getApiRequestInfo())) {
            ExtensionResponseDTO responseDTO = null;
            try {
                responseDTO = postProcessResponse(responseContextDTO);
                return createContinueExtensionResponseDTOFromExtensionPayload(responseDTO);
            } catch (OpenBankingAPIHandlerException e) {
                log.error("Error occurred in the post-auth-response-processing step!", e);
                return createErrorExtensionResponseDTO(responseContextDTO.getMsgInfo());
            }
        }

        if (nextHandler != null) {
            return nextHandler.handlePostProcessResponse(responseContextDTO);
        }

        return null;
    }

    protected boolean canProcess(MsgInfoDTO msgInfoDTO, APIRequestInfoDTO apiRequestInfoDTO) {
        return false;
    }

    protected ExtensionResponseDTO preProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        return null;
    }

    protected ExtensionResponseDTO postProcessRequest(RequestContextDTO requestContextDTO)
            throws OpenBankingAPIHandlerException {
        return null;
    }

    protected ExtensionResponseDTO preProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {
        return null;
    }

    protected ExtensionResponseDTO postProcessResponse(ResponseContextDTO responseContextDTO)
            throws OpenBankingAPIHandlerException {
        return null;
    }

    // Some utility functions

    protected static String getPayload(MsgInfoDTO msgInfoDTO) {
        String contentType = msgInfoDTO.getHeaders().get(HttpHeader.CONTENT_TYPE);

        if (contentType == null) {
            return null;
        }

        if (msgInfoDTO.getPayloadHandler() == null) {
            return null;
        }

        String payload = null;
        try {
            payload = msgInfoDTO.getPayloadHandler().consumeAsString();
        } catch (Exception e) {
            return null;
        }

        String[] xmlExtractionNeededPayloadTypes = {
                HttpHeaderContentType.APPLICATION_JWT,
                HttpHeaderContentType.APPLICATION_JOSE
        };

        for (String type : xmlExtractionNeededPayloadTypes) {
            if (contentType.startsWith(type)) {
                try {
                    payload = XML
                            .toJSONObject(payload)
                            .getJSONObject("soapenv:Body")
                            .getJSONObject("text")
                            .getString("content");
                    return payload;
                } catch (JSONException e) {
                    return null;
                }
            }
        }

        return payload;
    }

    private static ExtensionResponseDTO createErrorExtensionResponseDTO(MsgInfoDTO msgInfoDTO) {
        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();
        extensionResponseDTO.setPayload(
                new ByteArrayInputStream(
                        Objects.requireNonNull(getPayload(msgInfoDTO)).getBytes(StandardCharsets.UTF_8)
                )
        );
        extensionResponseDTO.setHeaders(msgInfoDTO.getHeaders());
        extensionResponseDTO.setResponseStatus(ExtensionResponseStatus.RETURN_ERROR.toString());
        extensionResponseDTO.setStatusCode(500);
        return extensionResponseDTO;
    }

    private static ExtensionResponseDTO createContinueExtensionResponseDTOFromExtensionPayload(
            ExtensionResponseDTO extensionResponseDTO
    ) {
        if (extensionResponseDTO == null) {
            return null;
        }
        extensionResponseDTO.setResponseStatus(ExtensionResponseStatus.CONTINUE.toString());
        return extensionResponseDTO;
    }
}
