package com.wso2.openbanking.uk.common.util;

import java.text.Collator;
import java.util.Locale;

public class StringUtil {
    public static String sanitizeString(String str) {
        return str.trim().replaceAll("[\r\n]", "_");
    }
}
