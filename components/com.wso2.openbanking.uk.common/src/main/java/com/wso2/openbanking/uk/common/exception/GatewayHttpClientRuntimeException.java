package com.wso2.openbanking.uk.common.exception;

/**
 * This class represents a runtime exception that can be thrown when an error occurs in the GatewayHttpClient.
 */
public class GatewayHttpClientRuntimeException extends RuntimeException {
    /**
     * Constructs a new GatewayHttpClientRuntimeException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause.
     */
    public GatewayHttpClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new GatewayHttpClientRuntimeException with the specified detail message.
     *
     * @param message The detail message.
     */
    public GatewayHttpClientRuntimeException(String message) {
        super(message);
    }
}
