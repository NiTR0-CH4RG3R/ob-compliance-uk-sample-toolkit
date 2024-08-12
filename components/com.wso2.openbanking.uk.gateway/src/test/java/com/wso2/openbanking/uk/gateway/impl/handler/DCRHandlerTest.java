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

package com.wso2.openbanking.uk.gateway.impl.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;
import org.wso2.carbon.apimgt.common.gateway.extensionlistener.PayloadHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

/**
 * This class tests the DCRHandler.
 */
public class DCRHandlerTest {
    DCRHandler dcrHandler = null;

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
            "    \"x5u\" : \"https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/7eJ8S_ZgvlYxFAFSghV9xMJ" +
            "ROvk.pem\",\n" +
            "    \"x5t#S256\" : \"xZbIpA5FEBBmyOTOZTXH4v4URSMckOAxDMNWrFRtqGE=\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"7x6UrhU-Yj1Aa9Ird03JJCcDurs\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"myUaNObS1bCecqALtY2pRJg3FuVRGQnKgPlS5KbOmwzmeVQEDFjEGDO1OjTrYskQvNRPMD_2SAsgwyCP7AbU" +
            "5LjvlAKB7KgObB_RI-4VEGXQbQCPEC8nPOzZqKPPGJhFspRriCY7Uo9TdeXPz2aLyXCNNmXG0aTcAHAEbh_mdrlNju5kyWBY9c" +
            "OdODsJzUfiasxsQWRRpeZvpmGEa2nQkvTj7gunXpBauCIeuWin4_xt36nCaY6Yu6CVMgSH6_33eP7u_wFdip2ZhDeeiI6Er3Kc" +
            "U8PUDu6uubvU-CS6r5aTkcK8yIErvNnhKf0UOi_KQMplMVgyWhUNcuGZ8NCuZw\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"tls\",\n" +
            "    \"x5c\" : [ \"MIIFODCCBCCgAwIBAgIEWcbiiTANBgkqhkiG9w0BAQsFADBTMQswCQYDVQQGEwJHQjEUMBIGA1UEChML" +
            "T3BlbkJhbmtpbmcxLjAsBgNVBAMTJU9wZW5CYW5raW5nIFByZS1Qcm9kdWN0aW9uIElzc3VpbmcgQ0EwHhcNMjMxMTE1MDUxMD" +
            "MxWhcNMjQxMjE1MDU0MDMxWjBhMQswCQYDVQQGEwJHQjEUMBIGA1UEChMLT3BlbkJhbmtpbmcxGzAZBgNVBAsTEjAwMTU4MDAw" +
            "MDFIUVFyWkFBWDEfMB0GA1UEAxMWakZRdVE0ZVFiTkNNU3FkQ29nMjFuRjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCgg" +
            "EBAJslGjTm0tWwnnKgC7WNqUSYNxblURkJyoD5UuSmzpsM5nlUBAxYxBgztTo062LJELzUTzA/9kgLIMMgj+wG1OS475QCgeyo" +
            "Dmwf0SPuFRBl0G0AjxAvJzzs2aijzxiYRbKUa4gmO1KPU3Xlz89mi8lwjTZlxtGk3ABwBG4f5na5TY7uZMlgWPXDnTg7Cc1H4m" +
            "rMbEFkUaXmb6ZhhGtp0JL04+4Lp16QWrgiHrlop+P8bd+pwmmOmLuglTIEh+v993j+7v8BXYqdmYQ3noiOhK9ynFPD1A7urrm7" +
            "1Pgkuq+Wk5HCvMiBK7zZ4Sn9FDovykDKZTFYMloVDXLhmfDQrmcCAwEAAaOCAgQwggIAMA4GA1UdDwEB/wQEAwIHgDAgBgNVHS" +
            "UBAf8EFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwgeAGA1UdIASB2DCB1TCB0gYLKwYBBAGodYEGAWQwgcIwKgYIKwYBBQUHAgEW" +
            "Hmh0dHA6Ly9vYi50cnVzdGlzLmNvbS9wb2xpY2llczCBkwYIKwYBBQUHAgIwgYYMgYNVc2Ugb2YgdGhpcyBDZXJ0aWZpY2F0ZS" +
            "Bjb25zdGl0dXRlcyBhY2NlcHRhbmNlIG9mIHRoZSBPcGVuQmFua2luZyBSb290IENBIENlcnRpZmljYXRpb24gUG9saWNpZXMg" +
            "YW5kIENlcnRpZmljYXRlIFByYWN0aWNlIFN0YXRlbWVudDBtBggrBgEFBQcBAQRhMF8wJgYIKwYBBQUHMAGGGmh0dHA6Ly9vYi" +
            "50cnVzdGlzLmNvbS9vY3NwMDUGCCsGAQUFBzAChilodHRwOi8vb2IudHJ1c3Rpcy5jb20vb2JfcHBfaXNzdWluZ2NhLmNydDA6" +
            "BgNVHR8EMzAxMC+gLaArhilodHRwOi8vb2IudHJ1c3Rpcy5jb20vb2JfcHBfaXNzdWluZ2NhLmNybDAfBgNVHSMEGDAWgBRQc5" +
            "HGIXLTd/T+ABIGgVx5eW4/UDAdBgNVHQ4EFgQU7T6cMtCSQTT5JWW3O6vifRUSdpkwDQYJKoZIhvcNAQELBQADggEBAE9jrd/A" +
            "E65vy3SEWdmFKPS4su7uEHy+KH18PETV6jMF2UFIJAOx7jl+5a3O66NkcpxFPeyvSuH+6tAAr2ZjpoQwtW9tZ9k2KSOdNOiJeQ" +
            "gjavwQC6t/BHI3yXWOIQm445BUN1cV9pagcRJjRyL3SPdHVoRfIbF7VI/+ULHwWdZYPXxtwUoda1mQFf6a+2lO4ziUHb3U8iD9" +
            "0FBURzID7WJ1ODSeB5zE/hG9Sxd9wlSXvl1oNmc/ha5oG/7rJpRqrx5Dcq3LEoX9iZZ3knHLkCm/abIQ7Nff8GQytuGhnGZxmG" +
            "FYKDXdKElcl9dAlZ3bIK2I+I6jD2z2XvSfrhFyRjU=\" ],\n" +
            "    \"x5t\" : \"i_rXxQv8kzzoPawCeJN_KdafnDA=\",\n" +
            "    \"x5u\" : \"https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/7x6UrhU-Yj1Aa9Ird03JJCc" +
            "Durs.pem\",\n" +
            "    \"x5t#S256\" : \"807-E8KgUMV6dRHTQi1_QYo5eyPvjmjbxCtunbFixV0=\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"Pn7wWY8GW2wX4sT8rKVgUSR0WJU\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"2-1zVMBojig-vQ--UopUalovrUQxHgSAJ6gwOJOH-mDCEXUly0KGxu60UfiTpLm0tf6UH74fNVH01DqfP6DL" +
            "9T17pk_RHDfALdSFRMU37iKZNlOB6uYrEODgL3-crOvgengMWNs9YoQR5gHF3uJJEczJh8PihRfbJOanjsHMdBwuGfDEMh38rQ" +
            "6P4LEeTc8MtdR2qNm3R3DfFJZu0GrR5g-xdSFZ4XV1ik8Ad4lGjATK-84clJmwsgjGLHvP57R6BvJjj8K8nPfQ2Cxd9der_KVV" +
            "6W5QMVokCk_fcAyfvrOAHILcYOC-NeB_EiIqEm4dxNH9RVzRf_7gxZNiMq6Q8Q\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"enc\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"_R4CIMA-v0c1SRr9eHzVC-PWj8Y\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"yUSmfxrRo3JIL4Ivvx_M-_fWiU-V63lw4S1TLr0zguI4ZBcCC8ivg8V7O-VWGd69W21V0afYmBs0rVOoz30G" +
            "4VChu84INM3izd-6us3GcNaIUyXMaXv75v4Cuj0zcdoaR0AGKflJAwuLTDZlkBpqFT6QDZ90kCOxxjAu0I3maL_4X6XeMm9StH" +
            "bK9lsmHU7jXhk980qQEextjbbsA3xjXsIVmi7xigMG2CTrmegEP233JuGJOi57R7gVpi0xR66firAw7OXY4fl7R1QKzX5iibDD" +
            "QqYOkxGk5MPP0dKPmxDezQvlLC8Cynh7x9QD_bagPIdtJlijw4HCHuehfVCsQQ\",\n" +
            "    \"e\" : \"AQAB\",\n" +
            "    \"use\" : \"enc\"\n" +
            "  }, {\n" +
            "    \"kid\" : \"u_XAGF_C6ehevwr0udpSGThM8Co\",\n" +
            "    \"kty\" : \"RSA\",\n" +
            "    \"n\" : \"0cw7xXPJgtUyzjgIUTPI-j30lgYtQRVpPJXVkCEvBL8xsR6t6OL6DnaVZMd54VuUweg5vW0-oMhVhFkshbxR" +
            "PXRDsBl0-wvUZegUyYmZ2vModazMlhAIbmZ6Xma2k15sURUWaVj4msfLrFrEML8CsHsxrhzc5lTDgtI3ZDq37pKv8CzDyzzxtd" +
            "gSng-DJCb1Arg5MhFuPZ1vE1dh8nqALoS4zOr9tNIcZod9GUEaX1G22b0XDO2caj8ePer9Tem_GG0JmQJA29vJN3WdroHeyAKk" +
            "qFqvmuCPYFfmv3du857N3hXoXxETX1dSLuWGob9-IArtstARg_n9vwaVeygIuQ\",\n" +
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
        httpServer.start();

        dcrHandler = new DCRHandler();
    }

    @AfterClass
    public void tearDown() {
        httpServer.stop(0);
    }


    @Test
    public void testCanProcessTrue() {
        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        boolean canProcess = dcrHandler.canProcess(msgInfoDTO, null);
        Assert.assertTrue(canProcess);
    }

    @Test
    public void testCanProcessFalse() {
        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/not_register");
        boolean canProcess = dcrHandler.canProcess(msgInfoDTO, null);
        Assert.assertFalse(canProcess);
    }

    @Test
    public void testPreProcessRequest() throws IOException, ParseException {
        String expectedPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":\"web\"," +
                "\"iss\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"aud\":\"https:\\/\\/localbank.com\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3Mi" +
                "OiJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjoiVGV" +
                "zdCIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpGUXVRNG" +
                "VRYk5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgKFNhbmRib" +
                "3gpIiwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRlZCBmb3Ig" +
                "dGVzdGluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaHR0cHM" +
                "6Ly93c28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRpcm" +
                "VjdF91cmlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlc" +
                "yI6WyJBSVNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJh" +
                "dXRob3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF" +
                "0dXMiOiJBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQU" +
                "lTUCIsIkNCUElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsib" +
                "WVtYmVyX3N0YXRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJp" +
                "IjoiaHR0cHM6Ly93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDA" +
                "xSFFRclpBQVgiLCJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG" +
                "5pY2FsIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2hua" +
                "WNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0" +
                "Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9qd2t" +
                "zIiwib3JnX2p3a3NfcmV2b2tlZF9lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9qd2tzIiwic29mdHdhcmVfan" +
                "drc19lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2VuZHBva" +
                "W50IjoiaHR0cDovL2xvY2FsaG9zdDo4MDAwL2p3a3MiLCJzb2Z0d2FyZV9wb2xpY3lfdXJpIjoiaHR0cHM6Ly93c28yLmNv" +
                "bSIsInNvZnR3YXJlX3Rvc191cmkiOiJodHRwczovL3dzbzIuY29tIiwic29mdHdhcmVfb25fYmVoYWxmX29mX29yZyI6Ild" +
                "TTzIgT3BlbiBCYW5raW5nIiwianRpIjoiZXRmOGFhcng3eGxxIiwiaWF0IjoxNzIzMTAwNjEzLCJleHAiOjIwMzg0NjA2MT" +
                "N9.aH0x8kaM_dkDL6UQHPkU7rHtHwICikSklBRC4HJkXQpLLkyqPFeAj5HPSPSKvfgPBWtyO0qfmRSQVpmdvsfMzht24h8V" +
                "SsRnhyeEUnOlG1YtGBtzl7-KDVLiNEYmX6vkXlhZa4mpr3Mm2InUBzguytzyrttEAj-vEEJgFH_HaKUmhWk-SlM713R6D7U" +
                "dMNOXyB5j6XtSQxyjdSbA7NNZ3icsc6ETBAI0tcw61XsI5vdgvKGJ1QkN-K31k8IPjxlVcfIHqHTfH_E-iT-RfoQ8Cr-fSU" +
                "5uGWufS3jI1uH_PJqKrGZ3caUabtjI6R3iHCOubXUgkCb3Rk_S-w7if61QTA\"," +
                "\"scope\":\"accounts payments\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"exp\":2038460613," +
                "\"iat\":1723100613," +
                "\"jti\":\"4j1bvqckfl9r\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}\n";

        String inputPayload =
                "<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<text xmlns=\"http://ws.apache.org/commons/ns/payload\">" +
                        "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3M" +
                        "iOiJqRlF1UTRlUWJOQ01TcWRDb2cyMW5GIiwiYXVkIjoiaHR0cHM6Ly9sb2NhbGJhbmsuY29tIiwic2NvcGUiOiJhY2N" +
                        "vdW50cyBwYXltZW50cyIsInRva2VuX2VuZHBvaW50X2F1dGhfbWV0aG9kIjoicHJpdmF0ZV9rZXlfand0IiwidG9rZW5" +
                        "fZW5kcG9pbnRfYXV0aF9zaWduaW5nX2FsZyI6IlBTMjU2IiwiZ3JhbnRfdHlwZXMiOlsiYXV0aG9yaXphdGlvbl9jb2R" +
                        "lIiwiY2xpZW50X2NyZWRlbnRpYWxzIiwicmVmcmVzaF90b2tlbiJdLCJyZXNwb25zZV90eXBlcyI6WyJjb2RlIGlkX3R" +
                        "va2VuIl0sImlkX3Rva2VuX3NpZ25lZF9yZXNwb25zZV9hbGciOiJQUzI1NiIsInJlcXVlc3Rfb2JqZWN0X3NpZ25pbmd" +
                        "fYWxnIjoiUFMyNTYiLCJhcHBsaWNhdGlvbl90eXBlIjoid2ViIiwic29mdHdhcmVfaWQiOiJqRlF1UTRlUWJOQ01TcWR" +
                        "Db2cyMW5GIiwicmVkaXJlY3RfdXJpcyI6WyJodHRwczovL3d3dy5nb29nbGUuY29tL3JlZGlyZWN0cy9yZWRpcmVjdDE" +
                        "iXSwianRpIjoiNGoxYnZxY2tmbDlyIiwiaWF0IjoxNzIzMTAwNjEzLCJleHAiOjIwMzg0NjA2MTMsInNvZnR3YXJlX3N" +
                        "0YXRlbWVudCI6ImV5SmhiR2NpT2lKUVV6STFOaUlzSW5SNWNDSTZJa3BYVkNJc0ltdHBaQ0k2SWpkbFNqaFRYMXBuZG1" +
                        "4WmVFWkJSbE5uYUZZNWVFMUtVazkyYXlKOS5leUpwYzNNaU9pSlBjR1Z1UW1GdWEybHVaeUJNZEdRaUxDSnpiMlowZDJ" +
                        "GeVpWOWxiblpwY205dWJXVnVkQ0k2SW5OaGJtUmliM2dpTENKemIyWjBkMkZ5WlY5dGIyUmxJam9pVkdWemRDSXNJbk5" +
                        "2Wm5SM1lYSmxYMmxrSWpvaWFrWlJkVkUwWlZGaVRrTk5VM0ZrUTI5bk1qRnVSaUlzSW5OdlpuUjNZWEpsWDJOc2FXVnV" +
                        "kRjlwWkNJNkltcEdVWFZSTkdWUllrNURUVk54WkVOdlp6SXhia1lpTENKemIyWjBkMkZ5WlY5amJHbGxiblJmYm1GdFp" +
                        "TSTZJbGRUVHpJZ1QzQmxiaUJDWVc1cmFXNW5JRlJRVURJZ0tGTmhibVJpYjNncElpd2ljMjltZEhkaGNtVmZZMnhwWlc" +
                        "1MFgyUmxjMk55YVhCMGFXOXVJam9pVkdocGN5QmhiSFJsY201aGRHbDJaU0JVVUZBZ2FYTWdZM0psWVhSbFpDQm1iM0l" +
                        "nZEdWemRHbHVaeUJ3ZFhKd2IzTmxjeTRnSWl3aWMyOW1kSGRoY21WZmRtVnljMmx2YmlJNk1TNDFMQ0p6YjJaMGQyRnl" +
                        "aVjlqYkdsbGJuUmZkWEpwSWpvaWFIUjBjSE02THk5M2MyOHlMbU52YlNJc0lteHZaMjlmZFhKcElqb2lhSFIwY0hNNkx" +
                        "5OTNkM2N1ZDNOdk1pNWpiMjB2ZDNOdk1pNXFjR2NpTENKemIyWjBkMkZ5WlY5eVpXUnBjbVZqZEY5MWNtbHpJanBiSW1" +
                        "oMGRIQnpPaTh2ZDNkM0xtZHZiMmRzWlM1amIyMHZjbVZrYVhKbFkzUnpMM0psWkdseVpXTjBNU0pkTENKemIyWjBkMkZ" +
                        "5WlY5eWIyeGxjeUk2V3lKQlNWTlFJaXdpVUVsVFVDSXNJa05DVUVsSklsMHNJbTl5WjJGdWFYTmhkR2x2Ymw5amIyMXd" +
                        "aWFJsYm5SZllYVjBhRzl5YVhSNVgyTnNZV2x0Y3lJNmV5SmhkWFJvYjNKcGRIbGZhV1FpT2lKUFFrZENVaUlzSW5KbFo" +
                        "ybHpkSEpoZEdsdmJsOXBaQ0k2SWxWdWEyNXZkMjR3TURFMU9EQXdNREF4U0ZGUmNscEJRVmdpTENKemRHRjBkWE1pT2l" +
                        "KQlkzUnBkbVVpTENKaGRYUm9iM0pwYzJGMGFXOXVjeUk2VzNzaWJXVnRZbVZ5WDNOMFlYUmxJam9pUjBJaUxDSnliMnh" +
                        "sY3lJNld5SlFTVk5RSWl3aVFVbFRVQ0lzSWtOQ1VFbEpJbDE5TEhzaWJXVnRZbVZ5WDNOMFlYUmxJam9pU1VVaUxDSnl" +
                        "iMnhsY3lJNld5SlFTVk5RSWl3aVEwSlFTVWtpTENKQlNWTlFJbDE5TEhzaWJXVnRZbVZ5WDNOMFlYUmxJam9pVGt3aUx" +
                        "DSnliMnhsY3lJNld5SlFTVk5RSWl3aVFVbFRVQ0lzSWtOQ1VFbEpJbDE5WFgwc0luTnZablIzWVhKbFgyeHZaMjlmZFh" +
                        "KcElqb2lhSFIwY0hNNkx5OTNjMjh5TG1OdmJTOTNjMjh5TG1wd1p5SXNJbTl5WjE5emRHRjBkWE1pT2lKQlkzUnBkbVV" +
                        "pTENKdmNtZGZhV1FpT2lJd01ERTFPREF3TURBeFNGRlJjbHBCUVZnaUxDSnZjbWRmYm1GdFpTSTZJbGRUVHpJZ0tGVkx" +
                        "LU0JNU1UxSlZFVkVJaXdpYjNKblgyTnZiblJoWTNSeklqcGJleUp1WVcxbElqb2lWR1ZqYUc1cFkyRnNJaXdpWlcxaGF" +
                        "Xd2lPaUp6WVdOb2FXNXBjMEIzYzI4eUxtTnZiU0lzSW5Cb2IyNWxJam9pS3prME56YzBNamMwTXpjMElpd2lkSGx3WlN" +
                        "JNklsUmxZMmh1YVdOaGJDSjlMSHNpYm1GdFpTSTZJa0oxYzJsdVpYTnpJaXdpWlcxaGFXd2lPaUp6WVdOb2FXNXBjMEI" +
                        "zYzI4eUxtTnZiU0lzSW5Cb2IyNWxJam9pS3prME56YzBNamMwTXpjMElpd2lkSGx3WlNJNklrSjFjMmx1WlhOekluMWR" +
                        "MQ0p2Y21kZmFuZHJjMTlsYm1Sd2IybHVkQ0k2SW1oMGRIQTZMeTlzYjJOaGJHaHZjM1E2T0RBd01DOXFkMnR6SWl3aWI" +
                        "zSm5YMnAzYTNOZmNtVjJiMnRsWkY5bGJtUndiMmx1ZENJNkltaDBkSEE2THk5c2IyTmhiR2h2YzNRNk9EQXdNQzlxZDJ" +
                        "0eklpd2ljMjltZEhkaGNtVmZhbmRyYzE5bGJtUndiMmx1ZENJNkltaDBkSEE2THk5c2IyTmhiR2h2YzNRNk9EQXdNQzl" +
                        "xZDJ0eklpd2ljMjltZEhkaGNtVmZhbmRyYzE5eVpYWnZhMlZrWDJWdVpIQnZhVzUwSWpvaWFIUjBjRG92TDJ4dlkyRnN" +
                        "hRzl6ZERvNE1EQXdMMnAzYTNNaUxDSnpiMlowZDJGeVpWOXdiMnhwWTNsZmRYSnBJam9pYUhSMGNITTZMeTkzYzI4eUx" +
                        "tTnZiU0lzSW5OdlpuUjNZWEpsWDNSdmMxOTFjbWtpT2lKb2RIUndjem92TDNkemJ6SXVZMjl0SWl3aWMyOW1kSGRoY21" +
                        "WZmIyNWZZbVZvWVd4bVgyOW1YMjl5WnlJNklsZFRUeklnVDNCbGJpQkNZVzVyYVc1bklpd2lhblJwSWpvaVpYUm1PR0Z" +
                        "oY25nM2VHeHhJaXdpYVdGMElqb3hOekl6TVRBd05qRXpMQ0psZUhBaU9qSXdNemcwTmpBMk1UTjkuYUgweDhrYU1fZGt" +
                        "ETDZVUUhQa1U3ckh0SHdJQ2lrU2tsQlJDNEhKa1hRcExMa3lxUEZlQWo1SFBTUFNLdmZnUEJXdHlPMHFmbVJTUVZwbWR" +
                        "2c2ZNemh0MjRoOFZTc1JuaHllRVVuT2xHMVl0R0J0emw3LUtEVkxpTkVZbVg2dmtYbGhaYTRtcHIzTW0ySW5VQnpndXl" +
                        "0enlydHRFQWotdkVFSmdGSF9IYUtVbWhXay1TbE03MTNSNkQ3VWRNTk9YeUI1ajZYdFNReHlqZFNiQTdOTlozaWNzYzZ" +
                        "FVEJBSTB0Y3c2MVhzSTV2ZGd2S0dKMVFrTi1LMzFrOElQanhsVmNmSUhxSFRmSF9FLWlULVJmb1E4Q3ItZlNVNXVHV3V" +
                        "mUzNqSTF1SF9QSnFLckdaM2NhVWFidGpJNlIzaUhDT3ViWFVna0NiM1JrX1MtdzdpZjYxUVRBIn0.KlozkbL1tNFJsvP" +
                        "xOgtWjpalPAAZThu4Vswtamoi8PSLT3ufMwm-NTYir7_HXl3gu_Lnl8dp1dOofRv1zfXXLfXi8ZWUb6K38h79rPIZEdY" +
                        "oAD31Y-0BhxjdQZgi0Y8HLmjSHZb4NsXeQIEIWPTk2rRIOklfCVPjbasFga0RafxhExGCN6N0BVhl1wnxTNN4o5sby0p" +
                        "oNw4c3E_90dEjNgTiAe-RrPUzMRgzxnFv-fjyBmZvw83G5jjgYfQZrEEbeHEIrnGdDfjgV1y6rwmO0GqvUsMYJ-I51-4" +
                        "kEUiE_69Dveinv_GrZOOkuVHmSluxxFDjx2EKCU_MzE2A-biYPw" +
                    "</text>" +
                "</soapenv:Body>";

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(
                new HashMap<String, String>() { {
                    put("Content-Type", "application/jwt");
                } }
        );
        msgInfoDTO.setHttpMethod("POST");
        msgInfoDTO.setPayloadHandler(new PayloadHandler() {
            @Override
            public String consumeAsString() throws Exception {
                return inputPayload;
            }

            @Override
            public InputStream consumeAsStream() throws Exception {
                return null;
            }
        });

        RequestContextDTO requestContextDTO = new RequestContextDTO();
        requestContextDTO.setMsgInfo(msgInfoDTO);

        ExtensionResponseDTO extensionResponseDTO =  dcrHandler.preProcessRequest(requestContextDTO);

        Assert.assertNotNull(extensionResponseDTO);
        Assert.assertEquals(extensionResponseDTO.getHeaders().get("Content-Type"), "application/json");

        String actualPayload = convert(extensionResponseDTO.getPayload());

        JSONObject expectedPayloadJson = (JSONObject) (new JSONParser()).parse(expectedPayload);
        JSONObject actualPayloadJson = (JSONObject) (new JSONParser()).parse(actualPayload);

        Assert.assertEquals(actualPayloadJson, expectedPayloadJson);
    }

    @Test
    public void testPostProcessRequest() throws IOException, ParseException {
        String modifiedPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":\"web\"," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"token_type_extension\":\"JWT\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3MiOiJP" +
                "cGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjoiVGVzdCIsInN" +
                "vZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpGUXVRNGVRYk5DTVNxZE" +
                "NvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgKFNhbmRib3gpIiwic29mdHdhc" +
                "mVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRlZCBmb3IgdGVzdGluZyBwdXJwb3Nl" +
                "cy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaHR0cHM6Ly93c28yLmNvbSIsImxvZ29" +
                "fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vd3" +
                "d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiUElTUCIsIkNCUElJI" +
                "l0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lz" +
                "dHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI" +
                "6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsibWVtYmVyX3N0YXRlIjoiSU" +
                "UiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQ" +
                "UlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0" +
                "dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiw" +
                "ib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKz" +
                "k0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yL" +
                "mNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2ludCI6Imh0dHA6" +
                "Ly9sb2NhbGhvc3Q6ODAwMC9qd2tzIiwib3JnX2p3a3NfcmV2b2tlZF9lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9" +
                "qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9qd2tzIiwic29mdHdhcmVfandrc1" +
                "9yZXZva2VkX2VuZHBvaW50IjoiaHR0cDovL2xvY2FsaG9zdDo4MDAwL2p3a3MiLCJzb2Z0d2FyZV9wb2xpY3lfdXJpIjoiaHR0c" +
                "HM6Ly93c28yLmNvbSIsInNvZnR3YXJlX3Rvc191cmkiOiJodHRwczovL3dzbzIuY29tIiwic29mdHdhcmVfb25fYmVoYWxmX29m" +
                "X29yZyI6IldTTzIgT3BlbiBCYW5raW5nIiwianRpIjoic3p5YjV0MGU0d2VlIiwiaWF0IjoxNzIzMDk2MDE0LCJleHAiOjE3MjM" +
                "wOTk2MTR9.YGUaliz1bKY_YRdnUVyBiKLhDiAvwddr9Lonpcu4ld2BRqfPBIl93YHEFnGGYCba76S6sa7u1oET1cb1CkSLtuKRz" +
                "cB4-cM2B76V2f2zPJM0jsuu5dXk1lM2R8XUf0YL7r276sEem_-qCYJBcOlhExcFwIHiagLdXfxnT6r6V2psQUj-iPoiyqh7YlyP" +
                "5m8pcQiTug4uysGo_J94AAkFFTC8pBZm2nVvIsVFRaw4z72PCmZZbIi4V-d77B0eRIVHQaC2CyMNQmcu7skm11ZHMmKGiPFjkiL" +
                "VIjhdxY3NOpQEx4KfTVDb8FMGUKcH06fk6Fs2QnR09IulOLraApUkLw\"," +
                "\"jwks_uri\":\"http:\\/\\/localhost:8000\\/jwks\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"tls_client_certificate_bound_access_tokens\":true," +
                "\"client_name\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}\n";

        String inputPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":\"web\"," +
                "\"iss\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"aud\":\"https:\\/\\/localbank.com\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3MiOi" +
                "JPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjoiVGVzdCI" +
                "sInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpGUXVRNGVRYk5D" +
                "TVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgKFNhbmRib3gpIiwic" +
                "29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRlZCBmb3IgdGVzdGluZy" +
                "BwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaHR0cHM6Ly93c28yLmN" +
                "vbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRpcmVjdF91cmlzIjpb" +
                "Imh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiU" +
                "ElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOi" +
                "JPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJ" +
                "hdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsi" +
                "bWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiL" +
                "CJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS" +
                "93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI" +
                "6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5p" +
                "c0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzI" +
                "iwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLC" +
                "Jvcmdfandrc19lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9qd2tzIiwib3JnX2p3a3NfcmV2b2tlZF9lbmRwb2l" +
                "udCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHA6Ly9sb2NhbGhv" +
                "c3Q6ODAwMC9qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cDovL2xvY2FsaG9zdDo4MDAwL2p3a" +
                "3MiLCJzb2Z0d2FyZV9wb2xpY3lfdXJpIjoiaHR0cHM6Ly93c28yLmNvbSIsInNvZnR3YXJlX3Rvc191cmkiOiJodHRwczovL3" +
                "dzbzIuY29tIiwic29mdHdhcmVfb25fYmVoYWxmX29mX29yZyI6IldTTzIgT3BlbiBCYW5raW5nIiwianRpIjoic3p5YjV0MGU" +
                "0d2VlIiwiaWF0IjoxNzIzMDk2MDE0LCJleHAiOjE3MjMwOTk2MTR9.YGUaliz1bKY_YRdnUVyBiKLhDiAvwddr9Lonpcu4ld2" +
                "BRqfPBIl93YHEFnGGYCba76S6sa7u1oET1cb1CkSLtuKRzcB4-cM2B76V2f2zPJM0jsuu5dXk1lM2R8XUf0YL7r276sEem_-q" +
                "CYJBcOlhExcFwIHiagLdXfxnT6r6V2psQUj-iPoiyqh7YlyP5m8pcQiTug4uysGo_J94AAkFFTC8pBZm2nVvIsVFRaw4z72PC" +
                "mZZbIi4V-d77B0eRIVHQaC2CyMNQmcu7skm11ZHMmKGiPFjkiLVIjhdxY3NOpQEx4KfTVDb8FMGUKcH06fk6Fs2QnR09IulOL" +
                "raApUkLw\"," +
                "\"scope\":\"accounts payments\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"exp\":1723099614," +
                "\"iat\":1723096014," +
                "\"jti\":\"53zo1dxr9m28\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(
                new HashMap<String, String>() { {
                    put("Content-Type", "application/json");
                } }
        );
        msgInfoDTO.setHttpMethod("POST");
        msgInfoDTO.setPayloadHandler(new PayloadHandler() {
            @Override
            public String consumeAsString() {
                return inputPayload;
            }

            @Override
            public InputStream consumeAsStream() {
                return null;
            }
        });

        RequestContextDTO requestContextDTO = new RequestContextDTO();
        requestContextDTO.setMsgInfo(msgInfoDTO);

        ExtensionResponseDTO extensionResponseDTO =  dcrHandler.postProcessRequest(requestContextDTO);

        Assert.assertNotNull(extensionResponseDTO);
        Assert.assertEquals(extensionResponseDTO.getHeaders().get("Content-Type"), "application/json");

        String actualPayload = convert(extensionResponseDTO.getPayload());
        JSONObject actualPayloadJson = (JSONObject) (new JSONParser()).parse(actualPayload);
        JSONObject modifiedPayloadJson = (JSONObject) (new JSONParser()).parse(modifiedPayload);

        Assert.assertEquals(actualPayloadJson, modifiedPayloadJson);

    }

    @Test
    public void testPreProcessResponse() throws IOException, ParseException {
        String inputPayload = "{" +
                "\"client_id\":\"vFMs7_Y_ih9t0exHL9nZUw7W5kwa\"," +
                "\"client_secret\":\"zFGfMcMr8HF7EbxQlpVzszVu5RGpYrEfTpAKbfu4y4Qa\"," +
                "\"client_secret_expires_at\":0," +
                "\"redirect_uris\":[\"https://www.google.com/redirects/redirect1\"]," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"client_name\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"ext_application_display_name\":null," +
                "\"ext_application_owner\":\"admin@carbon.super\"," +
                "\"ext_application_token_lifetime\":3600," +
                "\"ext_user_token_lifetime\":3600," +
                "\"ext_refresh_token_lifetime\":86400," +
                "\"ext_id_token_lifetime\":3600," +
                "\"ext_pkce_mandatory\":false," +
                "\"ext_pkce_support_plain\":false," +
                "\"ext_public_client\":false," +
                "\"token_type_extension\":\"JWT\"," +
                "\"ext_token_type\":\"JWT\"," +
                "\"jwks_uri\":" +
                "\"https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/jFQuQ4eQbNCMSqdCog21nF.jwks\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"sector_identifier_uri\":null," +
                "\"id_token_signed_response_alg\":\"PS256\"," +
                "\"id_token_encrypted_response_alg\":null," +
                "\"id_token_encrypted_response_enc\":null," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"tls_client_auth_subject_dn\":null," +
                "\"require_pushed_authorization_requests\":false," +
                "\"require_signed_request_object\":false," +
                "\"tls_client_certificate_bound_access_tokens\":true," +
                "\"subject_type\":\"public\",\"request_object_encryption_alg\":null," +
                "\"request_object_encryption_enc\":null," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3MiO" +
                "iJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjoiVGVzd" +
                "CIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpGUXVRNGVRY" +
                "k5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgKFNhbmRib3gpI" +
                "iwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRlZCBmb3IgdGVzd" +
                "GluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaHR0cHM6Ly93c" +
                "28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRpcmVjdF91c" +
                "mlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBS" +
                "VNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3Jpd" +
                "HlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY" +
                "3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCU" +
                "ElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0Y" +
                "XRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6L" +
                "y93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiL" +
                "CJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1ha" +
                "WwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZ" +
                "SI6IkJ1c2luZXNzIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6I" +
                "kJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51a" +
                "y8wMDE1ODAwMDAxSFFRclpBQVgvMDAxNTgwMDAwMUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50I" +
                "joiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwM" +
                "TU4MDAwMDFIUVFyWkFBWC5qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhb" +
                "mtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhc" +
                "mVfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4M" +
                "DAwMDFIUVFyWkFBWC9yZXZva2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91cmkiO" +
                "iJodHRwczovL3dzbzIuY29tIiwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZV9vbl9iZ" +
                "WhhbGZfb2Zfb3JnIjoiV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiI2M3J4eWtvcDA3eWEiLCJpYXQiOjE3MjMwODkwODgsI" +
                "mV4cCI6MTcyMzA5MjY4OH0.UDhht9PNvNJXJX7gn7cuSX-TeTlC4DW_wwJr1vSrAN7nTzqgf6OVbspULMXR4QTZ8Qfwnfjke" +
                "NmnU4OHyTMZ8PG8mCOND6OqOMsVIOP2xNBW3Gd08kQHIaz7Cjv5PCeJimfsxuBG7qqW6DNguKCgCarBwWyzWpYtWPcXSpcdR" +
                "_TnjQkWhoBrsOi0LNS18yvMhxxww271bdDilE28cZkB23_cS9L8630YcGBEkzlK9MFqIJm5qOdygm1BuYX01cC-SC4wGUmMa" +
                "BSn8YOWBuG7UwcIvv1QJ2OyxlsSUEdy1rn9fQfSJQRSqFIiAZfL-hyeaKRGxotTFovBXUMmv03BmA\"" +
                "}";

        String expectedPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":null," +
                "\"backchannel_user_code_parameter_supported\":false," +
                "\"tls_client_auth_subject_dn\":null," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"client_id\":\"vFMs7_Y_ih9t0exHL9nZUw7W5kwa\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3MiOi" +
                "JPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjoiVGVzdCI" +
                "sInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpGUXVRNGVRYk5D" +
                "TVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgKFNhbmRib3gpIiwic" +
                "29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRlZCBmb3IgdGVzdGluZy" +
                "BwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaHR0cHM6Ly93c28yLmN" +
                "vbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRpcmVjdF91cmlzIjpb" +
                "Imh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiU" +
                "ElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOi" +
                "JPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJ" +
                "hdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsi" +
                "bWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiL" +
                "CJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS" +
                "93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI" +
                "6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5p" +
                "c0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzI" +
                "iwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLC" +
                "Jvcmdfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFF" +
                "RclpBQVgvMDAxNTgwMDAwMUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlz" +
                "dG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIUVFyWkFBW" +
                "C5qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay" +
                "8wMDE1ODAwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2V" +
                "uZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZv" +
                "a2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91cmkiOiJodHRwczovL3dzbzIuY29tI" +
                "iwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZV9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMi" +
                "BPcGVuIEJhbmtpbmciLCJqdGkiOiI2M3J4eWtvcDA3eWEiLCJpYXQiOjE3MjMwODkwODgsImV4cCI6MTcyMzA5MjY4OH0.UDh" +
                "ht9PNvNJXJX7gn7cuSX-TeTlC4DW_wwJr1vSrAN7nTzqgf6OVbspULMXR4QTZ8QfwnfjkeNmnU4OHyTMZ8PG8mCOND6OqOMsV" +
                "IOP2xNBW3Gd08kQHIaz7Cjv5PCeJimfsxuBG7qqW6DNguKCgCarBwWyzWpYtWPcXSpcdR_TnjQkWhoBrsOi0LNS18yvMhxxww" +
                "271bdDilE28cZkB23_cS9L8630YcGBEkzlK9MFqIJm5qOdygm1BuYX01cC-SC4wGUmMaBSn8YOWBuG7UwcIvv1QJ2OyxlsSUE" +
                "dy1rn9fQfSJQRSqFIiAZfL-hyeaKRGxotTFovBXUMmv03BmA\"," +
                "\"client_secret_expires_at\":0," +
                "\"backchannel_client_notification_endpoint\":null," +
                "\"scope\":null," +
                "\"client_secret\":\"zFGfMcMr8HF7EbxQlpVzszVu5RGpYrEfTpAKbfu4y4Qa\"," +
                "\"client_id_issued_at\":1723096674," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"backchannel_authentication_request_signing_alg\":null," +
                "\"backchannel_token_delivery_mode\":\"poll\"," +
                "\"response_types\":[]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}\n";

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(
                new HashMap<String, String>() { {
                    put("Content-Type", "application/json");
                } }
        );
        msgInfoDTO.setHttpMethod("POST");
        msgInfoDTO.setPayloadHandler(new PayloadHandler() {
            @Override
            public String consumeAsString() throws Exception {
                return inputPayload;
            }

            @Override
            public InputStream consumeAsStream() throws Exception {
                return null;
            }
        });

        ResponseContextDTO responseContextDTO = new ResponseContextDTO();
        responseContextDTO.setMsgInfo(msgInfoDTO);
        responseContextDTO.setStatusCode(200);

        ExtensionResponseDTO extensionResponseDTO =  dcrHandler.preProcessResponse(responseContextDTO);

        Assert.assertNotNull(extensionResponseDTO);
        Assert.assertEquals(extensionResponseDTO.getHeaders().get("Content-Type"), "application/json");

        String actualPayload = convert(extensionResponseDTO.getPayload());
        JSONObject actualPayloadJson = (JSONObject) (new JSONParser()).parse(actualPayload);
        JSONObject expectedPayloadJson = (JSONObject) (new JSONParser()).parse(expectedPayload);

        // Remove client_id_issued_at from the actual payload as it is dynamic
        actualPayloadJson.remove("client_id_issued_at");
        expectedPayloadJson.remove("client_id_issued_at");

        Assert.assertEquals(actualPayloadJson, expectedPayloadJson);

    }

    public static String convert(InputStream inputStream) throws IOException {
        // Using StringBuilder to accumulate the string
        StringBuilder result = new StringBuilder();

        // Wrap the InputStream with InputStreamReader and BufferedReader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }
}
