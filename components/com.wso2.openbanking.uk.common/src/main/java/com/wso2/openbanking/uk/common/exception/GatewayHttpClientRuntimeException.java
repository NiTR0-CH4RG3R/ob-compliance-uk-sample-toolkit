package com.wso2.openbanking.uk.common.exception;

public class GatewayHttpClientRuntimeException extends RuntimeException {
    public GatewayHttpClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayHttpClientRuntimeException(String message) {
        super(message);
    }
}
