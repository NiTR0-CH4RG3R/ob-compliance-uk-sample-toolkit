package com.wso2.openbanking.uk.gateway.exception;

/**
 * This class represents the runtime exception that can be thrown by the JWTValidator.
 */
public class JWTValidatorRuntimeException extends RuntimeException {

    /**
     * Constructs a new JWTValidatorRuntimeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public JWTValidatorRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new JWTValidatorRuntimeException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public JWTValidatorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
