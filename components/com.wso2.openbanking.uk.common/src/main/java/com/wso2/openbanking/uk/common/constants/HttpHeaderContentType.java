/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wso2.openbanking.uk.common.constants;

/**
 * This class contains http header content types used in the application. It is recommended to use these constants when
 * setting or getting http headers. It will help to avoid typos and ensure consistency.
 */
public class HttpHeaderContentType {
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JWT = "application/jwt";
    public static final String APPLICATION_JOSE = "application/jose";
}
