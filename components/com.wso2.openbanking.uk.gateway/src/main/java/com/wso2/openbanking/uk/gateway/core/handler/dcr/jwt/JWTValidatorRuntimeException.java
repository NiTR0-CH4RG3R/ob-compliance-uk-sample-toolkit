package com.wso2.openbanking.uk.gateway.core.handler.dcr.jwt;

public class JWTValidatorRuntimeException extends RuntimeException {
    public JWTValidatorRuntimeException(String message) {
        super(message);
    }

    public JWTValidatorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
