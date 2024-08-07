package com.wso2.openbanking.uk.common.util;

/**
 * An annotation to make methods skip code coverage. Use only with a valid reason to skip
 * code coverage.
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SkipTestCoverage {
    String message();
}
