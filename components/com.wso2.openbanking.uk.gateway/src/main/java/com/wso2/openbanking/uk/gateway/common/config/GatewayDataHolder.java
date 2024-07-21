package com.wso2.openbanking.uk.gateway.common.config;

import com.wso2.openbanking.uk.gateway.apimapplication.constants.DevPortalEndpoint;
import com.wso2.openbanking.uk.gateway.apimapplication.interfaces.APIMApplicationConfigProvider;
import com.wso2.openbanking.uk.gateway.common.constants.GatewayURL;
import com.wso2.openbanking.uk.gateway.common.interfaces.GatewayURLProvider;
import com.wso2.openbanking.uk.gateway.handler.core.OpenBankingAPIHandler;
import com.wso2.openbanking.uk.gateway.core.handler.dcr.DCRHandler;
import com.wso2.openbanking.uk.gateway.common.constants.GatewayConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the data generated at runtime required for the gateway component.
 */
public class GatewayDataHolder
        implements
        GatewayURLProvider,
        APIMApplicationConfigProvider {
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

    private OpenBankingAPIHandler handlerChain = null;
    private final Map<String, String> urlMap = new HashMap<>();

    private GatewayDataHolder() {
        constructHandlerChain();
        constructUrlMap();
    }

    public OpenBankingAPIHandler getHandlerChain() {
        return handlerChain;
    }

    private void constructHandlerChain() {
        handlerChain = new OpenBankingAPIHandler();
        handlerChain.setNextHandler(new DCRHandler());
    }

    private void constructUrlMap() {
        // TODO : Get the AM and IS host from the apimgt configuration provider. Only use the below values as defaults.
        urlMap.put(getEnumToStringWithType(GatewayURL.AM_HOST), GatewayConstants.DEFAULT_AM_HOST);
        urlMap.put(getEnumToStringWithType(GatewayURL.IS_HOST), GatewayConstants.DEFAULT_IS_HOST);


        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.APP_GET),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_GET);
        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.APP_CREATION),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_CREATION);
        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.APP_UPDATE),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_UPDATE);
        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.APP_DELETE),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_DELETE);
        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.APP_MAP_KEYS),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_APP_MAP_KEYS);
        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.API_LIST),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_API_LIST);
        urlMap.put(getEnumToStringWithType(DevPortalEndpoint.API_LIST_SUBSCRIBE),
                GatewayConstants.RESOURCE_MAP_DEV_PORTAL_API_LIST_SUBSCRIBE);
    }

    private static <E extends Enum<E>> String getEnumToStringWithType(E enumConstant) {
        String enumTypeName = enumConstant.getDeclaringClass().getSimpleName();
        String enumConstantName = enumConstant.name();
        return enumTypeName + "." + enumConstantName;
    }

    @Override
    public String getGatewayURL(GatewayURL endpoint) {
        return urlMap.get(getEnumToStringWithType(endpoint));
    }

    @Override
    public String getDevPortalEndpoint(DevPortalEndpoint endpoint) {
        return urlMap.get(getEnumToStringWithType(endpoint));
    }
}
