package com.wso2.openbanking.uk.common.exception;

/**
 * This class is the exception class for the Open Banking API. If any component in the toolkit encounters an error, it
 * must throw an OpenBankingException or a custom exception type that is derived from this type.
 */
public class OpenBankingException extends Exception {
    /**
     * Constructs a new OpenBankingException with the specified detail message.
     *
     * @param message The detail message.
     */
    public OpenBankingException(String message) {
            super(message);
    }

    /**
     * Constructs a new OpenBankingException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause.
     */
    public OpenBankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
