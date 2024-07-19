package com.wso2.openbanking.uk.gateway.config;

import com.wso2.openbanking.uk.gateway.handler.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.handler.dcr.DCRHandler;
import com.wso2.openbanking.uk.gateway.constants.GatewayConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the data generated at runtime required for the gateway component.
 */
public class GatewayDataHolder {
    private static volatile GatewayDataHolder instance = null;

    public static GatewayDataHolder getInstance() {
        if (instance == null) {
            synchronized (GatewayDataHolder.class) {
                if (instance == null) {
                    instance = new GatewayDataHolder();
                }
            }
        }
        return instance;
    }

    private final Map<String, String> devPortalResourcesMap = new HashMap<>();

    private OpenBankingAPIHandler handlerChain = null;
    private final Map<String, String> urlMap = new HashMap<>();

    private GatewayDataHolder() {
        constructHandlerChain();
        constructDevPortalResourceMap();
        constructUrlMap();
    }

    public OpenBankingAPIHandler getHandlerChain() {
        return handlerChain;
    }

    public String getUrl(String key) {
        return urlMap.get(key);
    }

    private void constructHandlerChain() {
        handlerChain = new OpenBankingAPIHandler();
        handlerChain.setNextHandler(new DCRHandler());
    }

    private void constructDevPortalResourceMap() {
        // TODO : Read from a configuration file.

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_GET,
                "/api/am/devportal/v3/applications/{applicationId}"
        );

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_CREATION,
                "/api/am/devportal/v3/applications"
        );

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_UPDATE,
                "/api/am/devportal/v3/applications/{applicationId}"
        );

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_DELETE,
                "/api/am/devportal/v3/applications/{applicationId}"
        );

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_MAP_KEYS,
                "/api/am/devportal/v3/applications/{applicationId}/map-keys"
        );

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_API_LIST,
                "api/am/devportal/v3/apis"
        );

        devPortalResourcesMap.put(
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_API_LIST_SUBSCRIBE,
                "/api/am/devportal/v3/subscriptions/multiple"
        );
    }

    private void constructUrlMap() {
        // TODO : Read from a configuration file.
        urlMap.put(GatewayConstants.URL_MAP_IS_HOST, "https://localhost:9446");
        urlMap.put(GatewayConstants.URL_MAP_AM_HOST, "https://localhost:9443");

        // Construct the URL map for the dev portal resources.
        String devPortalAppGetResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_GET);
        String devPortalAppGetUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST) + devPortalAppGetResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_APP_GET, devPortalAppGetUrl);

        String devPortalAppCreationResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_CREATION);
        String devPortalAppCreationUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST) + devPortalAppCreationResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_APP_CREATION, devPortalAppCreationUrl);

        String devPortalAppUpdateResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_UPDATE);
        String devPortalAppUpdateUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST) + devPortalAppUpdateResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_APP_UPDATE, devPortalAppUpdateUrl);

        String devPortalAppDeleteResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_DELETE);
        String devPortalAppDeleteUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST) + devPortalAppDeleteResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_APP_DELETE, devPortalAppDeleteUrl);

        String devPortalAppMapKeysResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_MAP_KEYS);
        String devPortalAppMapKeysUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST) + devPortalAppMapKeysResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_APP_MAP_KEYS, devPortalAppMapKeysUrl);

        String devPortalApiListResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_API_LIST);
        String devPortalApiListUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST) + devPortalApiListResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_API_LIST, devPortalApiListUrl);

        String devPortalApiListSubscribeResource =
                devPortalResourcesMap.get(GatewayConstants.RESOURCE_MAP_DEV_PORTAL_API_LIST_SUBSCRIBE);
        String devPortalApiListSubscribeUrl = urlMap.get(GatewayConstants.URL_MAP_AM_HOST)
                + devPortalApiListSubscribeResource;
        urlMap.put(GatewayConstants.URL_MAP_DEV_PORTAL_API_LIST_SUBSCRIBE, devPortalApiListSubscribeUrl);
    }

}
