package com.wso2.openbanking.uk.gateway.common.constants;

public class GatewayConstants {
    // NOTE : These are the default values. These can be overridden by the configurations.
    public static final String DEFAULT_IS_HOST = "https://localhost:9446";
    public static final String DEFAULT_AM_HOST = "https://localhost:9443";

    public static final String RESOURCE_MAP_DEV_PORTAL_APP_GET =
            "/api/am/devportal/v3/applications/{applicationId}";
    public static final String RESOURCE_MAP_DEV_PORTAL_APP_CREATION =
            "/api/am/devportal/v3/applications";
    public static final String RESOURCE_MAP_DEV_PORTAL_APP_UPDATE =
            "/api/am/devportal/v3/applications/{applicationId}";
    public static final String RESOURCE_MAP_DEV_PORTAL_APP_DELETE =
            "/api/am/devportal/v3/applications/{applicationId}";
    public static final String RESOURCE_MAP_DEV_PORTAL_APP_MAP_KEYS =
            "/api/am/devportal/v3/applications/{applicationId}/map-keys";
    public static final String RESOURCE_MAP_DEV_PORTAL_API_LIST =
            "api/am/devportal/v3/apis";
    public static final String RESOURCE_MAP_DEV_PORTAL_API_LIST_SUBSCRIBE =
            "/api/am/devportal/v3/subscriptions/multiple";
}
