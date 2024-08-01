package com.wso2.openbanking.uk.gateway.core.handler.dcr.apimapplication;

public class APIMApplicationRuntimeException extends RuntimeException {
    public APIMApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIMApplicationRuntimeException(String message) {
        super(message);
    }
}
