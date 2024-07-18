package com.wso2.openbanking.uk.gateway.handler.exception;

import com.wso2.openbanking.uk.common.exception.OpenBankingException;

/**
 * This class is the exception class for the Open Banking API handler. If any component in the toolkit encounters an
 * error while handling an API request, it must throw an OpenBankingAPIHandlerException or a custom exception type that
 * is derived from this type.
 */
public class OpenBankingAPIHandlerException extends OpenBankingException {
    public OpenBankingAPIHandlerException(String message) {
        super(message);
    }

    public OpenBankingAPIHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
