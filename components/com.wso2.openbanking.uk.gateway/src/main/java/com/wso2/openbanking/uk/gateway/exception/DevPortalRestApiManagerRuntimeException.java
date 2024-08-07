package com.wso2.openbanking.uk.gateway.exception;

/**
 * This class represents the runtime exception that can be thrown by the DevPortalRestApiManager.
 */
public class DevPortalRestApiManagerRuntimeException extends RuntimeException {
    /**
     * Constructs a new DevPortalRestApiManagerRuntimeException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public DevPortalRestApiManagerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DevPortalRestApiManagerRuntimeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public DevPortalRestApiManagerRuntimeException(String message) {
        super(message);
    }
}
