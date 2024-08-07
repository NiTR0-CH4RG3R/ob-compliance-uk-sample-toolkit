package com.wso2.openbanking.uk.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StringUtilTest {
    @Test
    public void testSanitizeString() {
        String str = "Hello\nWorld";
        String actual = StringUtil.sanitizeString(str);
        String expected = "Hello_World";
        Assert.assertEquals(actual, expected);
    }
}
