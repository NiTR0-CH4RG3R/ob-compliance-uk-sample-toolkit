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

package com.wso2.openbanking.uk.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * This class contains utility methods related to http requests and responses.
 */
public class HttpUtil {

    /**
     * Generates a basic authentication header using the given username and password.
     *
     * @param username The username to be used in the basic authentication header.
     * @param password The password to be used in the basic authentication header.
     * @return The basic authentication header.
     */
    public static String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + new String(
                Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
    }

    /**
     * Converts the given data to x-www-form-urlencoded format.
     *
     * @param data The data to be converted. The keys are the parameter names and the values are the parameter values.
     * @return The x-www-form-urlencoded data.
     */
    public static String convertToXWWWFormUrlEncoded(Map<String, String> data) {
        StringBuilder encodedData = new StringBuilder();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            encodedData.append(entry.getKey());
            encodedData.append("=");
            encodedData.append(entry.getValue());
            encodedData.append("&");
        }

        encodedData.deleteCharAt(encodedData.length() - 1);

        return encodedData.toString();
    }

    /**
     * Concatenates the given parameters to the given url.
     *
     * @param url The url to which the parameters should be concatenated.
     * @param params The parameters to be concatenated. The keys are the parameter names and the values are the
     *               parameter values.
     * @return The url with the parameters concatenated.
     */
    public static String concatParamsToUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey());
            urlBuilder.append("=");
            urlBuilder.append(entry.getValue());
            urlBuilder.append("&");
        }

        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        return urlBuilder.toString();
    }

    /**
     * Generates a bearer authentication header using the given token.
     *
     * @param token The token to be used in the bearer authentication header.
     * @return The bearer authentication header.
     */
    public static String generateBearerAuthHeader(String token) {
        return "Bearer " + token;
    }

    /**
     * Extracts the path variable sent as the last segment of the given resource.
     *
     * @param resource The resource from which the path variable should be extracted.
     * @return The path variable sent as the last segment of the resource.
     */
    public static String extractPathVariableSentAsLastSegment(String resource) {
        // If the resource ends with a "/", then remove it
        if (resource.endsWith("/")) {
            resource = resource.substring(0, resource.length() - 1);
        }

        // Split the resource by "/"
        String[] segments = resource.split("/");

        // Return the last segment
        return segments[segments.length - 1];
    }

    /**
     * Extracts the bearer token from the given authorization header.
     *
     * @param authorizationHeader The authorization header from which the bearer token should be extracted.
     * @return The bearer token extracted from the authorization header.
     */
    public static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }

        String[] parts = authorizationHeader.trim().split("\\s+");;
        if (parts.length != 2) {
            return null;
        }

        String scheme = parts[0];
        String token = parts[1];

        if (!"Bearer".equals(scheme)) {
            return null;
        }

        return token;
    }
}
