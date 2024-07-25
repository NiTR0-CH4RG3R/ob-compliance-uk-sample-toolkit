package com.wso2.openbanking.uk.gateway.core.handler.dcr.apimapplication;


public class APIMApplication {

    private static final String DEFAULT_AM_HOST = "https://localhost:9443";
    private static final String RESOURCE_DEV_PORTAL_APP_GET = "/api/am/devportal/v3/applications/{applicationId}";
    private static final String RESOURCE_DEV_PORTAL_APP_CREATION = "/api/am/devportal/v3/applications";
    private static final String RESOURCE_DEV_PORTAL_APP_UPDATE = "/api/am/devportal/v3/applications/{applicationId}";
    private static final String RESOURCE_DEV_PORTAL_APP_DELETE = "/api/am/devportal/v3/applications/{applicationId}";
    private static final String RESOURCE_DEV_PORTAL_APP_MAP_KEYS =
            "/api/am/devportal/v3/applications/{applicationId}/map-keys";
    private static final String RESOURCE_DEV_PORTAL_API_LIST = "/api/am/devportal/v3/apis";
    private static final String RESOURCE_DEV_PORTAL_API_LIST_SUBSCRIBE = "/api/am/devportal/v3/subscriptions/multiple";

    private final String amHost;
    private final String devPortalAppGetURL;
    private final String devPortalAppCreationURL;
    private final String devPortalAppUpdateURL;
    private final String devPortalAppDeleteURL;
    private final String devPortalAppMapKeysURL;
    private final String devPortalApiListURL;
    private final String devPortalApiListSubscribeURL;


    public APIMApplication(String amHost) {
        this.amHost = amHost == null || amHost.isBlank() ? DEFAULT_AM_HOST : amHost;

        this.devPortalAppGetURL = this.amHost + RESOURCE_DEV_PORTAL_APP_GET;
        this.devPortalAppCreationURL = this.amHost + RESOURCE_DEV_PORTAL_APP_CREATION;
        this.devPortalAppUpdateURL = this.amHost + RESOURCE_DEV_PORTAL_APP_UPDATE;
        this.devPortalAppDeleteURL = this.amHost + RESOURCE_DEV_PORTAL_APP_DELETE;
        this.devPortalAppMapKeysURL = this.amHost + RESOURCE_DEV_PORTAL_APP_MAP_KEYS;
        this.devPortalApiListURL = this.amHost + RESOURCE_DEV_PORTAL_API_LIST;
        this.devPortalApiListSubscribeURL = this.amHost + RESOURCE_DEV_PORTAL_API_LIST_SUBSCRIBE;
    }

    public void createApplication() {
        // Create application
    }




}
