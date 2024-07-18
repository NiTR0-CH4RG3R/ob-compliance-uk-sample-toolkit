package com.wso2.openbanking.uk.common.exception;

/**
 * This class is the runtime exception class for the Open Banking API. If any component in the toolkit decides to throw
 * a runtime exception, it must throw an OpenBankingRuntimeException or an exception type that is derived from this.
 */
public class OpenBankingRuntimeException extends RuntimeException{
    public OpenBankingRuntimeException(String message) {
        super(message);
    }

    public OpenBankingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
