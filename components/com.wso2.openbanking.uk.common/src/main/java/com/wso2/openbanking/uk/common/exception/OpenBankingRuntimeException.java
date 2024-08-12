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

package com.wso2.openbanking.uk.common.exception;

/**
 * This class is the runtime exception class for the Open Banking API. If any component in the toolkit decides to throw
 * a runtime exception, it must throw an OpenBankingRuntimeException or an exception type that is derived from this.
 */
public class OpenBankingRuntimeException extends RuntimeException {

    /**
     * Constructs a new OpenBankingRuntimeException with the specified detail message.
     *
     * @param message The detail message.
     */
    public OpenBankingRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new OpenBankingRuntimeException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause.
     */
    public OpenBankingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
