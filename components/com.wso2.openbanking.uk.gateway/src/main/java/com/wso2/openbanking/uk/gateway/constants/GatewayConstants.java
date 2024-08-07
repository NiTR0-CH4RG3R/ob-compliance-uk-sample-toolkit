package com.wso2.openbanking.uk.gateway.constants;

/**
 * This class contains the constants used in the Gateway. Please keep in mind that the constants that start with
 * DEFAULT_* are just the default values and can be overridden by the configurations. So please refer to the
 * configurations for the actual values.
 */
public class GatewayConstants {
    public static final String DEFAULT_IS_HOST = "https://localhost:9446";
    public static final String DEFAULT_AM_HOST = "https://localhost:9443";

    public static final String DEFAULT_AM_USERNAME = "admin";
    public static final String DEFAULT_AM_PASSWORD = "admin";

    public static final String KEY_MANAGER_NAME = "IS7KM";
    public static final String AM_TAG_REGULATORY = "regulatory";
}
