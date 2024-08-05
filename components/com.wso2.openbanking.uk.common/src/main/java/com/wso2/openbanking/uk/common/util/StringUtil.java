package com.wso2.openbanking.uk.common.util;

import java.text.Collator;
import java.util.Locale;

public class StringUtil {
    public static boolean equalsIgnoreCase(String str1, String str2) {
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        collator.setStrength(Collator.PRIMARY); // For case-insensitive comparison
        return (collator.compare(str1, str2) == 0);
    }

    public static String sanitizeString(String str) {
        return str.trim().replaceAll("[\r\n]", "_");
    }
}
