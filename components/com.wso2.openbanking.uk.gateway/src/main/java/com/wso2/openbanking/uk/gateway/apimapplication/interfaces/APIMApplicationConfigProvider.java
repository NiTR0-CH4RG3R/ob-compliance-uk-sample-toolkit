package com.wso2.openbanking.uk.gateway.apimapplication.interfaces;

import com.wso2.openbanking.uk.gateway.apimapplication.constants.DevPortalEndpoint;

public interface APIMApplicationConfigProvider {
    String getDevPortalEndpoint(DevPortalEndpoint endpoint);
}
