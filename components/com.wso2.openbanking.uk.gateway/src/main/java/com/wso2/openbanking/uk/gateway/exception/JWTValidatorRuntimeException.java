package com.wso2.openbanking.uk.gateway.exception;

public class JWTValidatorRuntimeException extends RuntimeException {
    public JWTValidatorRuntimeException(String message) {
        super(message);
    }

    public JWTValidatorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
