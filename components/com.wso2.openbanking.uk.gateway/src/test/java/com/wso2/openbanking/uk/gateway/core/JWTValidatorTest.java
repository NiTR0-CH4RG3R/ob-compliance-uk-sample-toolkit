package com.wso2.openbanking.uk.gateway.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wso2.openbanking.uk.gateway.exception.JWTValidatorRuntimeException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * This class tests the JWTValidator.
 */
public class JWTValidatorTest {

    InetSocketAddress address = new InetSocketAddress(8000);
    HttpServer httpServer = null;

    String jwkSetEndpointResponse = "{\n" +
            "  \"keys\" : [ {\n" +
            "    \"kid\" : \"7eJ8S_ZgvlYxFAFSghV9xMJROvk\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"qLfocPSmvHU3el9p8lzqlIyDSLSM6JCu35ZnoamEpSEDYmtsHFZO9ptfVDuGJ-XlRuQE6SefdILKoGmU9KqS" +
            "xgt09JyRvYntRUuPvo7tGQQugUP69KtDwVYSfyVe_hSJLzKW-Wsg9rz6lW0DA64jf9gWaoHWVweAEjwQQsNRYddKnc6GRwqOub" +
            "vq3WxDPS2yaLlodlHcVdvR0AJA750ZqQh6urNSYB1xwu1a5IkxP5vtzsgMOb8hH8xXuKiXTjJ4GQ6Vat4go6odTXo8jc389IwS" +
            "33okBQpn-fRULTHhgdoFgNfbux_gbKm5uCaRx3ghsb_xcS4erciTzigSk_lARQ\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"sig\",\n" +
            "    \"x5c\" : [ \"MIIFLTCCBBWgAwIBAgIEWcbiiDANBgkqhkiG9w0BAQsFADBTMQswCQYDVQQGEwJHQjEUMBIGA1UEChML" +
            "T3BlbkJhbmtpbmcxLjAsBgNVBAMTJU9wZW5CYW5raW5nIFByZS1Qcm9kdWN0aW9uIElzc3VpbmcgQ0EwHhcNMjMxMTE1MDUxMD" +
            "A4WhcNMjQxMjE1MDU0MDA4WjBhMQswCQYDVQQGEwJHQjEUMBIGA1UEChMLT3BlbkJhbmtpbmcxGzAZBgNVBAsTEjAwMTU4MDAw" +
            "MDFIUVFyWkFBWDEfMB0GA1UEAxMWakZRdVE0ZVFiTkNNU3FkQ29nMjFuRjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCgg" +
            "EBAKi36HD0prx1N3pfafJc6pSMg0i0jOiQrt+WZ6GphKUhA2JrbBxWTvabX1Q7hifl5UbkBOknn3SCyqBplPSqksYLdPSckb2J" +
            "7UVLj76O7RkELoFD+vSrQ8FWEn8lXv4UiS8ylvlrIPa8+pVtAwOuI3/YFmqB1lcHgBI8EELDUWHXSp3OhkcKjrm76t1sQz0tsm" +
            "i5aHZR3FXb0dACQO+dGakIerqzUmAdccLtWuSJMT+b7c7IDDm/IR/MV7iol04yeBkOlWreIKOqHU16PI3N/PSMEt96JAUKZ/n0" +
            "VC0x4YHaBYDX27sf4Gypubgmkcd4IbG/8XEuHq3Ik84oEpP5QEUCAwEAAaOCAfkwggH1MA4GA1UdDwEB/wQEAwIGwDAVBgNVHS" +
            "UEDjAMBgorBgEEAYI3CgMMMIHgBgNVHSAEgdgwgdUwgdIGCysGAQQBqHWBBgFkMIHCMCoGCCsGAQUFBwIBFh5odHRwOi8vb2Iu" +
            "dHJ1c3Rpcy5jb20vcG9saWNpZXMwgZMGCCsGAQUFBwICMIGGDIGDVXNlIG9mIHRoaXMgQ2VydGlmaWNhdGUgY29uc3RpdHV0ZX" +
            "MgYWNjZXB0YW5jZSBvZiB0aGUgT3BlbkJhbmtpbmcgUm9vdCBDQSBDZXJ0aWZpY2F0aW9uIFBvbGljaWVzIGFuZCBDZXJ0aWZp" +
            "Y2F0ZSBQcmFjdGljZSBTdGF0ZW1lbnQwbQYIKwYBBQUHAQEEYTBfMCYGCCsGAQUFBzABhhpodHRwOi8vb2IudHJ1c3Rpcy5jb2" +
            "0vb2NzcDA1BggrBgEFBQcwAoYpaHR0cDovL29iLnRydXN0aXMuY29tL29iX3BwX2lzc3VpbmdjYS5jcnQwOgYDVR0fBDMwMTAv" +
            "oC2gK4YpaHR0cDovL29iLnRydXN0aXMuY29tL29iX3BwX2lzc3VpbmdjYS5jcmwwHwYDVR0jBBgwFoAUUHORxiFy03f0/gASBo" +
            "FceXluP1AwHQYDVR0OBBYEFKjCef/JxD+ND9eSb7hQlmEhSxUqMA0GCSqGSIb3DQEBCwUAA4IBAQCnKH9FdLmJMruX2qfbrpT0" +
            "qaV8bP7xa9UDRYSMsAWC2kqCxs8CJmARt5+xsxBW6P65+mkLS2vXgQl7J8RTMiQVnHJvvNaldYnV6odsYOqvv+vGib8Qe0gKWS" +
            "jih+Gd1Ct4UQFtn6P3ph+6OBB0OieZb7DYXqPJrX5UlG7K2fQ40MdFgBdeQZ3iNkXi43UIrQ5cF4cjYavmEFRmYeHya8AKfNCi" +
            "Wly15mNazW/X6SWf7pz+yk/l+gBv0wm3QT7ANXGf8izgoh6T5fmixPXSbdn8RUIV0kXp2TRRZ+CYUWBPJc3PvRXiiEEo2eHLXf" +
            "EHG2jzrt1iKnjk6hzuC1hUzK0t\" ],\n" +
            "    \"x5t\" : \"ov8shjyB_TDmFYr4e-1DQThUMSs=\",\n" +
            "    \"x5u\" : \"https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/7eJ8S_ZgvlYxFAFSghV9xM" +
            "JROvk.pem\",\n" +
            "    \"x5t#S256\" : \"xZbIpA5FEBBmyOTOZTXH4v4URSMckOAxDMNWrFRtqGE=\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"7x6UrhU-Yj1Aa9Ird03JJCcDurs\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"myUaNObS1bCecqALtY2pRJg3FuVRGQnKgPlS5KbOmwzmeVQEDFjEGDO1OjTrYskQvNRPMD_2SAsgwyCP7AbU5" +
            "LjvlAKB7KgObB_RI-4VEGXQbQCPEC8nPOzZqKPPGJhFspRriCY7Uo9TdeXPz2aLyXCNNmXG0aTcAHAEbh_mdrlNju5kyWBY9cOd" +
            "ODsJzUfiasxsQWRRpeZvpmGEa2nQkvTj7gunXpBauCIeuWin4_xt36nCaY6Yu6CVMgSH6_33eP7u_wFdip2ZhDeeiI6Er3KcU8P" +
            "UDu6uubvU-CS6r5aTkcK8yIErvNnhKf0UOi_KQMplMVgyWhUNcuGZ8NCuZw\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"tls\",\n" +
            "    \"x5c\" : [ \"MIIFODCCBCCgAwIBAgIEWcbiiTANBgkqhkiG9w0BAQsFADBTMQswCQYDVQQGEwJHQjEUMBIGA1UEChMLT" +
            "3BlbkJhbmtpbmcxLjAsBgNVBAMTJU9wZW5CYW5raW5nIFByZS1Qcm9kdWN0aW9uIElzc3VpbmcgQ0EwHhcNMjMxMTE1MDUxMDMx" +
            "WhcNMjQxMjE1MDU0MDMxWjBhMQswCQYDVQQGEwJHQjEUMBIGA1UEChMLT3BlbkJhbmtpbmcxGzAZBgNVBAsTEjAwMTU4MDAwMDF" +
            "IUVFyWkFBWDEfMB0GA1UEAxMWakZRdVE0ZVFiTkNNU3FkQ29nMjFuRjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJ" +
            "slGjTm0tWwnnKgC7WNqUSYNxblURkJyoD5UuSmzpsM5nlUBAxYxBgztTo062LJELzUTzA/9kgLIMMgj+wG1OS475QCgeyoDmwf0" +
            "SPuFRBl0G0AjxAvJzzs2aijzxiYRbKUa4gmO1KPU3Xlz89mi8lwjTZlxtGk3ABwBG4f5na5TY7uZMlgWPXDnTg7Cc1H4mrMbEFk" +
            "UaXmb6ZhhGtp0JL04+4Lp16QWrgiHrlop+P8bd+pwmmOmLuglTIEh+v993j+7v8BXYqdmYQ3noiOhK9ynFPD1A7urrm71Pgkuq+" +
            "Wk5HCvMiBK7zZ4Sn9FDovykDKZTFYMloVDXLhmfDQrmcCAwEAAaOCAgQwggIAMA4GA1UdDwEB/wQEAwIHgDAgBgNVHSUBAf8EFj" +
            "AUBggrBgEFBQcDAQYIKwYBBQUHAwIwgeAGA1UdIASB2DCB1TCB0gYLKwYBBAGodYEGAWQwgcIwKgYIKwYBBQUHAgEWHmh0dHA6L" +
            "y9vYi50cnVzdGlzLmNvbS9wb2xpY2llczCBkwYIKwYBBQUHAgIwgYYMgYNVc2Ugb2YgdGhpcyBDZXJ0aWZpY2F0ZSBjb25zdGl0" +
            "dXRlcyBhY2NlcHRhbmNlIG9mIHRoZSBPcGVuQmFua2luZyBSb290IENBIENlcnRpZmljYXRpb24gUG9saWNpZXMgYW5kIENlcnR" +
            "pZmljYXRlIFByYWN0aWNlIFN0YXRlbWVudDBtBggrBgEFBQcBAQRhMF8wJgYIKwYBBQUHMAGGGmh0dHA6Ly9vYi50cnVzdGlzLm" +
            "NvbS9vY3NwMDUGCCsGAQUFBzAChilodHRwOi8vb2IudHJ1c3Rpcy5jb20vb2JfcHBfaXNzdWluZ2NhLmNydDA6BgNVHR8EMzAxM" +
            "C+gLaArhilodHRwOi8vb2IudHJ1c3Rpcy5jb20vb2JfcHBfaXNzdWluZ2NhLmNybDAfBgNVHSMEGDAWgBRQc5HGIXLTd/T+ABIG" +
            "gVx5eW4/UDAdBgNVHQ4EFgQU7T6cMtCSQTT5JWW3O6vifRUSdpkwDQYJKoZIhvcNAQELBQADggEBAE9jrd/AE65vy3SEWdmFKPS" +
            "4su7uEHy+KH18PETV6jMF2UFIJAOx7jl+5a3O66NkcpxFPeyvSuH+6tAAr2ZjpoQwtW9tZ9k2KSOdNOiJeQgjavwQC6t/BHI3yX" +
            "WOIQm445BUN1cV9pagcRJjRyL3SPdHVoRfIbF7VI/+ULHwWdZYPXxtwUoda1mQFf6a+2lO4ziUHb3U8iD90FBURzID7WJ1ODSeB" +
            "5zE/hG9Sxd9wlSXvl1oNmc/ha5oG/7rJpRqrx5Dcq3LEoX9iZZ3knHLkCm/abIQ7Nff8GQytuGhnGZxmGFYKDXdKElcl9dAlZ3b" +
            "IK2I+I6jD2z2XvSfrhFyRjU=\" ],\n" +
            "    \"x5t\" : \"i_rXxQv8kzzoPawCeJN_KdafnDA=\",\n" +
            "    \"x5u\" : \"https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/7x6UrhU-Yj1Aa9Ird03JJCcD" +
            "urs.pem\",\n" +
            "    \"x5t#S256\" : \"807-E8KgUMV6dRHTQi1_QYo5eyPvjmjbxCtunbFixV0=\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"Pn7wWY8GW2wX4sT8rKVgUSR0WJU\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"2-1zVMBojig-vQ--UopUalovrUQxHgSAJ6gwOJOH-mDCEXUly0KGxu60UfiTpLm0tf6UH74fNVH01DqfP6DL9" +
            "T17pk_RHDfALdSFRMU37iKZNlOB6uYrEODgL3-crOvgengMWNs9YoQR5gHF3uJJEczJh8PihRfbJOanjsHMdBwuGfDEMh38rQ6P" +
            "4LEeTc8MtdR2qNm3R3DfFJZu0GrR5g-xdSFZ4XV1ik8Ad4lGjATK-84clJmwsgjGLHvP57R6BvJjj8K8nPfQ2Cxd9der_KVV6W5" +
            "QMVokCk_fcAyfvrOAHILcYOC-NeB_EiIqEm4dxNH9RVzRf_7gxZNiMq6Q8Q\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"enc\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"_R4CIMA-v0c1SRr9eHzVC-PWj8Y\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"yUSmfxrRo3JIL4Ivvx_M-_fWiU-V63lw4S1TLr0zguI4ZBcCC8ivg8V7O-VWGd69W21V0afYmBs0rVOoz30G4" +
            "VChu84INM3izd-6us3GcNaIUyXMaXv75v4Cuj0zcdoaR0AGKflJAwuLTDZlkBpqFT6QDZ90kCOxxjAu0I3maL_4X6XeMm9StHbK" +
            "9lsmHU7jXhk980qQEextjbbsA3xjXsIVmi7xigMG2CTrmegEP233JuGJOi57R7gVpi0xR66firAw7OXY4fl7R1QKzX5iibDDQqY" +
            "OkxGk5MPP0dKPmxDezQvlLC8Cynh7x9QD_bagPIdtJlijw4HCHuehfVCsQQ\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"enc\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"u_XAGF_C6ehevwr0udpSGThM8Co\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"0cw7xXPJgtUyzjgIUTPI-j30lgYtQRVpPJXVkCEvBL8xsR6t6OL6DnaVZMd54VuUweg5vW0-oMhVhFkshbxRP" +
            "XRDsBl0-wvUZegUyYmZ2vModazMlhAIbmZ6Xma2k15sURUWaVj4msfLrFrEML8CsHsxrhzc5lTDgtI3ZDq37pKv8CzDyzzxtdgS" +
            "ng-DJCb1Arg5MhFuPZ1vE1dh8nqALoS4zOr9tNIcZod9GUEaX1G22b0XDO2caj8ePer9Tem_GG0JmQJA29vJN3WdroHeyAKkqFq" +
            "vmuCPYFfmv3du857N3hXoXxETX1dSLuWGob9-IArtstARg_n9vwaVeygIuQ\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"sig\"\n" +
            "  } ]\n" +
            "}";

    @BeforeClass
    public void setUp() throws NoSuchAlgorithmException, KeyManagementException {
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

        try {
            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpHandler handler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = jwkSetEndpointResponse.getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/jwks", handler);

        HttpHandler handlerWrongJWKS = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "{}".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };


        httpServer.createContext("/njwks", handlerWrongJWKS);

        httpServer.start();
    }

    @AfterClass
    public void tearDown() {
        httpServer.stop(0);
    }

    @Test
    public void testValidateSignatureUsingJWKSWithValidJWT() {
        String jwt = "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRta" +
                "W4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.jKT31t8RseZLeSDtN-4t5_IG33J-SzoRChoQq8OwdRspElX0_sM4b6labjjsL8Ht" +
                "hNRq8EEx0l3USd6mgTB0bFNCafGEGEnbgOVuK74n8gdCJCpI20_5osXNWxifdbSWTCFNiY0THNzm5C8Po2BSN5s2PN3eHqXmK72" +
                "kJnWOStVHTWwGbagbPsR3_NKe6DmlTN0VA-TeH0VL9sY7-4rApzPtTv7YtMJIAUebG9zha47piNY2W1bQkTE98FVevy-0r1oeC3" +
                "PqesjwlIfSbCBFutsfjUVsOe7nAWJIKIHaxfFNU2IXWbvniSfrnot_I1CELQ29GC9bNnRinPKxHnPvNQ";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        String jwkSetURL = "http://localhost:8000/jwks";

        boolean result = false;
        try {
            result = jwtValidator.validateSignatureUsingJWKS(jwkSetURL);
        } catch (Exception e) {
            Assert.fail("Error occurred while validating the signature using JWKS");
        }

        Assert.assertTrue(result);
    }

    @Test
    public void testValidateSignatureUsingJWKSWithInvalidJWT() {
        String jwt = "This is not a valid JWT";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        String jwkSetURL = "http://localhost:8000/jwks";

        boolean result = false;
        try {
            result = jwtValidator.validateSignatureUsingJWKS(jwkSetURL);
            Assert.fail("Error occurred while validating the signature using JWKS");
        } catch (JWTValidatorRuntimeException e) {
            Assert.assertEquals(e.getMessage(), "Invalid JWT format");
        }

        Assert.assertFalse(result);
    }

    @Test
    public void testValidateSignatureUsingJWKSWithInvalidURL() {
        String jwt = "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRta" +
                "W4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.jKT31t8RseZLeSDtN-4t5_IG33J-SzoRChoQq8OwdRspElX0_sM4b6labjjsL8Ht" +
                "hNRq8EEx0l3USd6mgTB0bFNCafGEGEnbgOVuK74n8gdCJCpI20_5osXNWxifdbSWTCFNiY0THNzm5C8Po2BSN5s2PN3eHqXmK72" +
                "kJnWOStVHTWwGbagbPsR3_NKe6DmlTN0VA-TeH0VL9sY7-4rApzPtTv7YtMJIAUebG9zha47piNY2W1bQkTE98FVevy-0r1oeC3" +
                "PqesjwlIfSbCBFutsfjUVsOe7nAWJIKIHaxfFNU2IXWbvniSfrnot_I1CELQ29GC9bNnRinPKxHnPvNQ";


        JWTValidator jwtValidator = new JWTValidator(jwt);

        String jwkSetURL = "ll/jwks";

        boolean result = false;
        try {
            result = jwtValidator.validateSignatureUsingJWKS(jwkSetURL);
            Assert.fail("Error occurred while validating the signature using JWKS");
        } catch (JWTValidatorRuntimeException e) {
            Assert.assertEquals(e.getMessage(), "Invalid JWKS endpoint");
        }

        Assert.assertFalse(result);
    }

    @Test
    public void testValidateSignatureUsingJWKSWithValidURL() {
        String jwt = "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRta" +
                "W4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.jKT31t8RseZLeSDtN-4t5_IG33J-SzoRChoQq8OwdRspElX0_sM4b6labjjsL8Ht" +
                "hNRq8EEx0l3USd6mgTB0bFNCafGEGEnbgOVuK74n8gdCJCpI20_5osXNWxifdbSWTCFNiY0THNzm5C8Po2BSN5s2PN3eHqXmK72" +
                "kJnWOStVHTWwGbagbPsR3_NKe6DmlTN0VA-TeH0VL9sY7-4rApzPtTv7YtMJIAUebG9zha47piNY2W1bQkTE98FVevy-0r1oeC3" +
                "PqesjwlIfSbCBFutsfjUVsOe7nAWJIKIHaxfFNU2IXWbvniSfrnot_I1CELQ29GC9bNnRinPKxHnPvNQ";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        String jwkSetURL = "http://localhost:8000/njwks";

        boolean result = false;
        try {
            result = jwtValidator.validateSignatureUsingJWKS(jwkSetURL);
        } catch (JWTValidatorRuntimeException e) {
            Assert.fail("Error occurred while validating the signature using JWKS");
        }

        Assert.assertFalse(result);
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
    public void testGetJWT() {
        String validJWT =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0Ijo" +
                        "xNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(validJWT);

        Assert.assertEquals(jwtValidator.getJwt(), validJWT);
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
    public void testGetClaimWrongType() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF" +
                "0IjoxNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        try {
            int sub = jwtValidator.getClaim("sub", int.class);
            Assert.fail("Error occurred while getting the claim");
        } catch (JWTValidatorRuntimeException e) {
            Assert.assertEquals(e.getMessage(),
                        String.format("Invalid claim type! Expected %s, but found %s",
                                int.class.getName(), String.class.getName()
                                )
                    );
        }
    }

    @Test
    public void testGetClaimNonExistingKey() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF" +
                "0IjoxNTE2MjM5MDIyfQ.-BJ02694Yp2IKXNSwomxVfMk8tVWtYm6PthMw8LIqOo";

        JWTValidator jwtValidator = new JWTValidator(jwt);


        String lol = jwtValidator.getClaim("lol", String.class);

        Assert.assertNull(lol);

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
    public void testGetClaimsWithInvalidJWT() {
        String jwt = "Not a valid JWT";

        JWTValidator jwtValidator = new JWTValidator(jwt);

        try {
            Map<String, Object> claims = jwtValidator.getClaims();
            Assert.fail("Error occurred while getting the claims");
        } catch (JWTValidatorRuntimeException e) {
            Assert.assertEquals(e.getMessage(), "Invalid JWT");
        }

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
