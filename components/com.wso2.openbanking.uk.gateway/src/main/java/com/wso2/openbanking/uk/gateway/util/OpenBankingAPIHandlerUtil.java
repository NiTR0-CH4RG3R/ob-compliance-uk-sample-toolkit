package com.wso2.openbanking.uk.gateway.util;

import com.wso2.openbanking.uk.common.constants.HttpHeader;
import com.wso2.openbanking.uk.common.constants.HttpHeaderContentType;
import org.json.JSONException;
import org.json.XML;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseStatus;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class OpenBankingAPIHandlerUtil {
    /**
     * Extract the payload as a string received from the request or the response.
     *
     * @param msgInfoDTO  MsgInfoDTO object received from the RequestContextDTO or ResponseContextDTO.
     * @return The extracted payload.
     */
    public static String getPayload(MsgInfoDTO msgInfoDTO) {
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

    /**
     * Create an ExtensionResponseDTO object for the RETURN_RESPONSE response status. You must use this method when you
     * want to stop the execution of the request/response flow inside the handler and return a response.
     *
     * @param statusCode        The status code of the response.
     * @param payload           The payload of the response.
     * @param headers           The headers of the response.
     * @param customProperties  The custom properties of the response.
     * @param status            The status of the response.
     * @return                  The ExtensionResponseDTO object.
     */
    public static ExtensionResponseDTO createExtensionResponseDTO (
            int statusCode,
            String payload,
            Map<String, String> headers,
            Map<String, Object> customProperties,
            ExtensionResponseStatus status
    ) {
        ExtensionResponseDTO extensionResponseDTO = new ExtensionResponseDTO();
        if (payload != null) {
            extensionResponseDTO.setPayload(
                    new ByteArrayInputStream(
                            payload.getBytes(StandardCharsets.UTF_8)
                    )
            );
        }
        extensionResponseDTO.setHeaders(headers);
        extensionResponseDTO.setResponseStatus(status.toString());
        extensionResponseDTO.setStatusCode(statusCode);
        extensionResponseDTO.setCustomProperty(customProperties);
        return extensionResponseDTO;
    }
}
