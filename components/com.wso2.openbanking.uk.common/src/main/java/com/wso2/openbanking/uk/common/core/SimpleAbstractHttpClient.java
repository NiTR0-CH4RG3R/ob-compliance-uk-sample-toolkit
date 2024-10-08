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

package com.wso2.openbanking.uk.common.core;

import com.wso2.openbanking.uk.common.model.SimpleHttpRequest;
import com.wso2.openbanking.uk.common.model.SimpleHttpResponse;

/**
 * This interface provides the contract for a simple abstract http client. The client should be able to send a
 * SimpleHttpRequest and return a SimpleHttpResponse.
 */
public interface SimpleAbstractHttpClient {
    /**
     * Sends a SimpleHttpRequest and returns a SimpleHttpResponse.
     *
     * @param request The SimpleHttpRequest to send.
     * @return The SimpleHttpResponse.
     */
    SimpleHttpResponse send(SimpleHttpRequest request);
}
