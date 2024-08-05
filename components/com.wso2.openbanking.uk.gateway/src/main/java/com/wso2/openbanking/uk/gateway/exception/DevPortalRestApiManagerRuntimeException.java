package com.wso2.openbanking.uk.gateway.exception;

public class DevPortalRestApiManagerRuntimeException extends RuntimeException {
    public DevPortalRestApiManagerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DevPortalRestApiManagerRuntimeException(String message) {
        super(message);
    }
}
