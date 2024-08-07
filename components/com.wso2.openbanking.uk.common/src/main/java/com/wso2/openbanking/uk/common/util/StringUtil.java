package com.wso2.openbanking.uk.common.util;

/**
 * This class contains utility methods for string manipulation.
 */
public class StringUtil {
    /**
     * Sanitizes the given string by trimming and replacing new lines with underscores.
     *
     * @param str The string to be sanitized.
     * @return The sanitized string.
     */
    public static String sanitizeString(String str) {
        return str.trim().replaceAll("[\r\n]", "_");
    }
}
