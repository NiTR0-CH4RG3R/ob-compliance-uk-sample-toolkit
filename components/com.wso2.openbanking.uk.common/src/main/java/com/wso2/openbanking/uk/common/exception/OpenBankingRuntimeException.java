package com.wso2.openbanking.uk.common.exception;

/**
 * This class is the runtime exception class for the Open Banking API. If any component in the toolkit decides to throw
 * a runtime exception, it must throw an OpenBankingRuntimeException or an exception type that is derived from this.
 */
public class OpenBankingRuntimeException extends RuntimeException {

    /**
     * Constructs a new OpenBankingRuntimeException with the specified detail message.
     *
     * @param message The detail message.
     */
    public OpenBankingRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new OpenBankingRuntimeException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause.
     */
    public OpenBankingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
