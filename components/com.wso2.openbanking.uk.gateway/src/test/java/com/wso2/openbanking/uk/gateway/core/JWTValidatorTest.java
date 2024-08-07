package com.wso2.openbanking.uk.gateway.core;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class tests the JWTValidator.
 */
public class JWTValidatorTest {

    @BeforeMethod
    public void setUp() throws Exception {
        // Bypass SSL certificate in order to test the signature validation using JWKSetURL. This is not required for
        // production code as we are already using openbanking.atlassian issued root and issuer certificates.
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) { }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) { }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        }, new java.security.SecureRandom());
        SSLContext.setDefault(sslContext);
    }


    @Test
    public void testValidateSignatureUsingJWKS() {
        String jwt =
                "eyJ0eXAiOiJKV1QiLCJraWQiOiI3ZUo4U19aZ3ZsWXhGQUZTZ2hWOXhNSlJPdmsiLCJhbGciOiJQUzI1NiJ9.eyJpc3MiOiJq" +
                        "RlF1UTRlUWJOQ01TcWRDb2cyMW5GIiwiaWF0IjoxNzE5MzA5OTg2LCJleHAiOjE3MjUxNzk1NDYsImp0aSI6IjE2N" +
                        "DMwOTI5MTY0OSIsImF1ZCI6Imh0dHBzOi8vbG9jYWxiYW5rLmNvbSIsInNjb3BlIjoiYWNjb3VudHMgcGF5bWVudH" +
                        "MiLCJ0b2tlbl9lbmRwb2ludF9hdXRoX21ldGhvZCI6InByaXZhdGVfa2V5X2p3dCIsInRva2VuX2VuZHBvaW50X2F" +
                        "1dGhfc2lnbmluZ19hbGciOiJQUzI1NiIsImdyYW50X3R5cGVzIjpbImF1dGhvcml6YXRpb25fY29kZSIsImNsaWVu" +
                        "dF9jcmVkZW50aWFscyIsInJlZnJlc2hfdG9rZW4iXSwicmVzcG9uc2VfdHlwZXMiOlsiY29kZSBpZF90b2tlbiJdL" +
                        "CJpZF90b2tlbl9zaWduZWRfcmVzcG9uc2VfYWxnIjoiUFMyNTYiLCJyZXF1ZXN0X29iamVjdF9zaWduaW5nX2FsZy" +
                        "I6IlBTMjU2IiwiYXBwbGljYXRpb25fdHlwZSI6IndlYiIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29" +
                        "nMjFuRiIsInJlZGlyZWN0X3VyaXMiOlsiaHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS9yZWRpcmVjdHMvcmVkaXJlY3Qx" +
                        "Il0sInNvZnR3YXJlX3N0YXRlbWVudCI6ImV5SmhiR2NpT2lKUVV6STFOaUlzSW10cFpDSTZJamRsU2poVFgxcG5kb" +
                        "XhaZUVaQlJsTm5hRlk1ZUUxS1VrOTJheUlzSW5SNWNDSTZJa3BYVkNKOS5leUpwYzNNaU9pSlBjR1Z1UW1GdWEybH" +
                        "VaeUJNZEdRaUxDSnBZWFFpT2pFM01UZzJNREUwTnpnc0ltcDBhU0k2SWpsa09HTTFZbVUyT1Rka01UUXpOalVpTEN" +
                        "KemIyWjBkMkZ5WlY5bGJuWnBjbTl1YldWdWRDSTZJbk5oYm1SaWIzZ2lMQ0p6YjJaMGQyRnlaVjl0YjJSbElqb2lW" +
                        "R1Z6ZENJc0luTnZablIzWVhKbFgybGtJam9pYWtaUmRWRTBaVkZpVGtOTlUzRmtRMjluTWpGdVJpSXNJbk52Wm5SM" +
                        "1lYSmxYMk5zYVdWdWRGOXBaQ0k2SW1wR1VYVlJOR1ZSWWs1RFRWTnhaRU52WnpJeGJrWWlMQ0p6YjJaMGQyRnlaVj" +
                        "lqYkdsbGJuUmZibUZ0WlNJNklsZFRUeklnVDNCbGJpQkNZVzVyYVc1bklGUlFVRElnS0ZOaGJtUmliM2dwSWl3aWM" +
                        "yOW1kSGRoY21WZlkyeHBaVzUwWDJSbGMyTnlhWEIwYVc5dUlqb2lWR2hwY3lCaGJIUmxjbTVoZEdsMlpTQlVVRkFn" +
                        "YVhNZ1kzSmxZWFJsWkNCbWIzSWdkR1Z6ZEdsdVp5QndkWEp3YjNObGN5NGdJaXdpYzI5bWRIZGhjbVZmZG1WeWMyb" +
                        "HZiaUk2TVM0MUxDSnpiMlowZDJGeVpWOWpiR2xsYm5SZmRYSnBJam9pYUhSMGNITTZMeTkzYzI4eUxtTnZiU0lzSW" +
                        "14dloyOWZkWEpwSWpvaWFIUjBjSE02THk5M2QzY3VkM052TWk1amIyMHZkM052TWk1cWNHY2lMQ0p6YjJaMGQyRnl" +
                        "aVjl5WldScGNtVmpkRjkxY21seklqcGJJbWgwZEhCek9pOHZkM2QzTG1kdmIyZHNaUzVqYjIwdmNtVmthWEpsWTNS" +
                        "ekwzSmxaR2x5WldOME1TSmRMQ0p6YjJaMGQyRnlaVjl5YjJ4bGN5STZXeUpCU1ZOUUlpd2lVRWxUVUNJc0lrTkNVR" +
                        "WxKSWwwc0ltOXlaMkZ1YVhOaGRHbHZibDlqYjIxd1pYUmxiblJmWVhWMGFHOXlhWFI1WDJOc1lXbHRjeUk2ZXlKaG" +
                        "RYUm9iM0pwZEhsZmFXUWlPaUpQUWtkQ1VpSXNJbkpsWjJsemRISmhkR2x2Ymw5cFpDSTZJbFZ1YTI1dmQyNHdNREU" +
                        "xT0RBd01EQXhTRkZSY2xwQlFWZ2lMQ0p6ZEdGMGRYTWlPaUpCWTNScGRtVWlMQ0poZFhSb2IzSnBjMkYwYVc5dWN5" +
                        "STZXM3NpYldWdFltVnlYM04wWVhSbElqb2lSMElpTENKeWIyeGxjeUk2V3lKUVNWTlFJaXdpUVVsVFVDSXNJa05DV" +
                        "UVsSklsMTlMSHNpYldWdFltVnlYM04wWVhSbElqb2lTVVVpTENKeWIyeGxjeUk2V3lKUVNWTlFJaXdpUTBKUVNVa2" +
                        "lMQ0pCU1ZOUUlsMTlMSHNpYldWdFltVnlYM04wWVhSbElqb2lUa3dpTENKeWIyeGxjeUk2V3lKUVNWTlFJaXdpUVV" +
                        "sVFVDSXNJa05DVUVsSklsMTlYWDBzSW5OdlpuUjNZWEpsWDJ4dloyOWZkWEpwSWpvaWFIUjBjSE02THk5M2MyOHlM" +
                        "bU52YlM5M2MyOHlMbXB3WnlJc0ltOXlaMTl6ZEdGMGRYTWlPaUpCWTNScGRtVWlMQ0p2Y21kZmFXUWlPaUl3TURFM" +
                        "U9EQXdNREF4U0ZGUmNscEJRVmdpTENKdmNtZGZibUZ0WlNJNklsZFRUeklnS0ZWTEtTQk1TVTFKVkVWRUlpd2liM0" +
                        "puWDJOdmJuUmhZM1J6SWpwYmV5SnVZVzFsSWpvaVZHVmphRzVwWTJGc0lpd2laVzFoYVd3aU9pSnpZV05vYVc1cGM" +
                        "wQjNjMjh5TG1OdmJTSXNJbkJvYjI1bElqb2lLemswTnpjME1qYzBNemMwSWl3aWRIbHdaU0k2SWxSbFkyaHVhV05o" +
                        "YkNKOUxIc2libUZ0WlNJNklrSjFjMmx1WlhOeklpd2laVzFoYVd3aU9pSnpZV05vYVc1cGMwQjNjMjh5TG1OdmJTS" +
                        "XNJbkJvYjI1bElqb2lLemswTnpjME1qYzBNemMwSWl3aWRIbHdaU0k2SWtKMWMybHVaWE56SW4xZExDSnZjbWRmYW" +
                        "5kcmMxOWxibVJ3YjJsdWRDSTZJbWgwZEhCek9pOHZhMlY1YzNSdmNtVXViM0JsYm1KaGJtdHBibWQwWlhOMExtOXl" +
                        "aeTUxYXk4d01ERTFPREF3TURBeFNGRlJjbHBCUVZndk1EQXhOVGd3TURBd01VaFJVWEphUVVGWUxtcDNhM01pTENK" +
                        "dmNtZGZhbmRyYzE5eVpYWnZhMlZrWDJWdVpIQnZhVzUwSWpvaWFIUjBjSE02THk5clpYbHpkRzl5WlM1dmNHVnVZb" +
                        "UZ1YTJsdVozUmxjM1F1YjNKbkxuVnJMekF3TVRVNE1EQXdNREZJVVZGeVdrRkJXQzl5WlhadmEyVmtMekF3TVRVNE" +
                        "1EQXdNREZJVVZGeVdrRkJXQzVxZDJ0eklpd2ljMjltZEhkaGNtVmZhbmRyYzE5bGJtUndiMmx1ZENJNkltaDBkSEJ" +
                        "6T2k4dmEyVjVjM1J2Y21VdWIzQmxibUpoYm10cGJtZDBaWE4wTG05eVp5NTFheTh3TURFMU9EQXdNREF4U0ZGUmNs" +
                        "cEJRVmd2YWtaUmRWRTBaVkZpVGtOTlUzRmtRMjluTWpGdVJpNXFkMnR6SWl3aWMyOW1kSGRoY21WZmFuZHJjMTl5W" +
                        "lhadmEyVmtYMlZ1WkhCdmFXNTBJam9pYUhSMGNITTZMeTlyWlhsemRHOXlaUzV2Y0dWdVltRnVhMmx1WjNSbGMzUX" +
                        "ViM0puTG5Wckx6QXdNVFU0TURBd01ERklVVkZ5V2tGQldDOXlaWFp2YTJWa0wycEdVWFZSTkdWUllrNURUVk54WkV" +
                        "Odlp6SXhia1l1YW5kcmN5SXNJbk52Wm5SM1lYSmxYM0J2YkdsamVWOTFjbWtpT2lKb2RIUndjem92TDNkemJ6SXVZ" +
                        "Mjl0SWl3aWMyOW1kSGRoY21WZmRHOXpYM1Z5YVNJNkltaDBkSEJ6T2k4dmQzTnZNaTVqYjIwaUxDSnpiMlowZDJGe" +
                        "VpWOXZibDlpWldoaGJHWmZiMlpmYjNKbklqb2lWMU5QTWlCUGNHVnVJRUpoYm10cGJtY2lmUS5iTnVmMDZSZUdDX0" +
                        "VKdDU5N3pBTTBsS2tjS2g3b0xrV1JCSEFGX3lpMXllVzZNNVNwUzJldkJtSUR6eTM0WG8wRnVscE9qQWFwVXhNT3M" +
                        "yMHA2S3dWckthT0FfQkgxMVZkMHNPSlhfbFRLRkVWaGY5NU1MSnVaNkFnRWZEVURQcW5yS09BampkQ19aNGZzWmVk" +
                        "LWs3Rkt2TjVxMlpMMjhyTmo0cFYtcnUyY0V6MXNkUVNEQlhndmxPajFmLXlMaFQ4VmxtV3FXUGJuZ1BDT0s1Z2dhU" +
                        "21zU0xPbkRvNFFUd2pSMlQtazBZbTRwajNITUZ6Y29acEczS0hTUjFWTzNiLW5aM2N3b29LUVRPdjZlRWpVRnBqOE" +
                        "90V0E5WGMxNHhEUm01aTN6RzBfTXpzS1l6VnNZM2FYUUhoNzVibm5HSlp4RVRvd1MyaVhYNE9LOFdaZXRZcncifQ." +
                        "N88rpn6ascX7Jp33A6WBETxIlvnL5bKTjRTbttDxfnZNO08yI093z6TdoNgWqEvNQlrLnMXoXxgLRXG8mxg49ZJWm" +
                        "Ic-t9wz7VOXa7IcIw6w1rPJYejvrW2NFd-UjdJNewkleCbdM9ymekZyCmzT5f9Y00-dDGBGpdG8-bAsXKztUVMjfp" +
                        "vgMLs2SLz9ZROXiFTlZ-mIzWKzu7cXCXoUAnGJY_JYIPRhOzAh-z7k4E30RYsBPmny-pb99CIzu7OVe9DHFjGhdfX" +
                        "yU28ICr4-6BzCCZPIGzoYt4x5nIq1WR6U_YeE_cdJBduFaK7ukcFAHqznGgesZydUxEGK1XCE-w";
        JWTValidator jwtValidator = new JWTValidator(jwt);

        String jwkSetURL = "https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/jFQuQ4eQbNCMSqdCog21nF.jwks";

        boolean result = false;
        try {
            result = jwtValidator.validateSignatureUsingJWKS(jwkSetURL);
        } catch (Exception e) {
            Assert.fail("Error occurred while validating the signature using JWKS");
        }

        Assert.assertTrue(result);
    }

    @Test
    public void testValidateJWT() {
        String validJWT =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0Ijo" +
                        "xNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(validJWT);

        boolean result = jwtValidator.validateJwt();

        Assert.assertTrue(result);

        String invalidJWT = "This is not a JWT";

        jwtValidator = new JWTValidator(invalidJWT);

        result = jwtValidator.validateJwt();

        Assert.assertFalse(result);
    }

    @Test
    public void testGetClaim() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF" +
                "0IjoxNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        String sub = jwtValidator.getClaim("sub", String.class);
        String name = jwtValidator.getClaim("name", String.class);
        Long iat = jwtValidator.getClaim("iat", Long.class);

        Assert.assertEquals(sub, "1234567890");
        Assert.assertEquals(name, "John Doe");
        Assert.assertEquals(iat, 1516239022);
    }

    @Test
    public void testGetClaims() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0" +
                "IjoxNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        Map<String, Object> claims = jwtValidator.getClaims();

        Assert.assertEquals(claims.size(), 3);
        Assert.assertEquals(claims.get("sub"), "1234567890");
        Assert.assertEquals(claims.get("name"), "John Doe");
        Assert.assertEquals(claims.get("iat"), (Long) 1516239022L);
    }

    @Test
    public void testGetJSONString() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0" +
                "IjoxNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        String jsonString = jwtValidator.getJSONString();

        Assert.assertEquals(jsonString, "{\"name\":\"John Doe\",\"sub\":\"1234567890\",\"iat\":1516239022}");
    }
}
