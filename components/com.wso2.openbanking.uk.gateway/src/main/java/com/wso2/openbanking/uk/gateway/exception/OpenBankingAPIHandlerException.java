package com.wso2.openbanking.uk.gateway.exception;

/**
 * This class is the exception class for the Open Banking API handler. If any component in the toolkit encounters an
 * error while handling an API request, it must throw an OpenBankingAPIHandlerException or a custom exception type that
 * is derived from this type.
 */
public class OpenBankingAPIHandlerException extends Exception {
    /**
     * Constructs a new OpenBankingAPIHandlerException with the specified detail message.
     *
     * @param message the detail message.
     */
    public OpenBankingAPIHandlerException(String message) {
        super(message);
    }

    /**
     * Constructs a new OpenBankingAPIHandlerException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public OpenBankingAPIHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
