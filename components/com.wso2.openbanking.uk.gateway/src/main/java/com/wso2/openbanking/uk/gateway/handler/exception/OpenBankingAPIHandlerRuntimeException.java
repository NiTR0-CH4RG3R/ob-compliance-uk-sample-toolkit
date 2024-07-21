package com.wso2.openbanking.uk.gateway.handler.exception;

/**
 * This class is the runtime exception class for the Open Banking API handler. If any component in the toolkit decides
 * to throw a runtime exception while handling an API request, it must throw an OpenBankingAPIHandlerRuntimeException or
 * a custom exception type that is derived from this type.
 */
public class OpenBankingAPIHandlerRuntimeException extends RuntimeException {
    public OpenBankingAPIHandlerRuntimeException(String message) {
        super(message);
    }

    public OpenBankingAPIHandlerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
