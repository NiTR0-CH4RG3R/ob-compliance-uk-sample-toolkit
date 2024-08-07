package com.wso2.openbanking.uk.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class HttpUtil {
    public static String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + new String(
                Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
    }

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

    public static String generateBearerAuthHeader(String token) {
        return "Bearer " + token;
    }

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
}
