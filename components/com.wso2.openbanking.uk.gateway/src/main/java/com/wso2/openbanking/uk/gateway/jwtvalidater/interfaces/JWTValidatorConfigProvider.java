package com.wso2.openbanking.uk.gateway.jwtvalidater.interfaces;

public interface JWTValidatorConfigProvider {
    int getConnectionTimeout();
    int getReadTimeout();
}
