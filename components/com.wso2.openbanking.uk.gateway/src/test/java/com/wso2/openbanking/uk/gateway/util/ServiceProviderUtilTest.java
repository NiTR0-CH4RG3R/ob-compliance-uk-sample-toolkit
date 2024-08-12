package com.wso2.openbanking.uk.gateway.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
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

/**
 * This class tests the ServiceProviderUtil.
 */
public class ServiceProviderUtilTest {


    @BeforeClass
    public void setUp() throws NoSuchAlgorithmException, KeyManagementException {
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
    public void testRevokeToken() {
        String validAccessToken = "eyJ4NXQiOiJPV1JpTXpaaVlURXhZVEl4WkdGa05UVTJOVE0zTWpkaFltTmxNVFZrTnpRMU56a3paV" +
                "Gc1TVRrNE0yWmxOMkZoWkdaalpURmlNemxsTTJJM1l6ZzJNZyIsImtpZCI6Ik9XUmlNelppWVRFeFlUSXhaR0ZrTlRVMk5U" +
                "TTNNamRoWW1ObE1UVmtOelExTnprelpUZzVNVGs0TTJabE4yRmhaR1pqWlRGaU16bGxNMkkzWXpnMk1nX1JTMjU2IiwidHl" +
                "wIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJuTVpVbUEyaGxmYnNRVnZmb293YjR1SVFsMmdhIiwiYXV0Ijo" +
                "iQVBQTElDQVRJT04iLCJiaW5kaW5nX3R5cGUiOiJjZXJ0aWZpY2F0ZSIsImlzcyI6Imh0dHBzOlwvXC9sb2NhbGhvc3Q6OT" +
                "Q0Nlwvb2F1dGgyXC90b2tlbiIsImNsaWVudF9pZCI6Im5NWlVtQTJobGZic1FWdmZvb3diNHVJUWwyZ2EiLCJhdWQiOiJuT" +
                "VpVbUEyaGxmYnNRVnZmb293YjR1SVFsMmdhIiwibmJmIjoxNzIzNDQxODQxLCJhenAiOiJuTVpVbUEyaGxmYnNRVnZmb293" +
                "YjR1SVFsMmdhIiwib3JnX2lkIjoiMTAwODRhOGQtMTEzZi00MjExLWEwZDUtZWZlMzZiMDgyMjExIiwiY25mIjp7Ing1dCN" +
                "TMjU2IjoiODA3LUU4S2dVTVY2ZFJIVFFpMV9RWW81ZXlQdmptamJ4Q3R1bmJGaXhWMCJ9LCJleHAiOjE3MjM0NDU0NDEsIm" +
                "9yZ19uYW1lIjoiU3VwZXIiLCJpYXQiOjE3MjM0NDE4NDEsImJpbmRpbmdfcmVmIjoiYWU2YmM4NzE1NGRhMjEwYTdjM2FjN" +
                "2I1ZjlhZDVjNjciLCJqdGkiOiIwNmViNTc2ZS02MzQ0LTRmYTMtODNiYy04MDEzZGZkYWVlOGYifQ.Vnu6mUjlqRFfyLFhg" +
                "JFvLqGvxFiegWlNF-bt7fXg6feZLCEqp5kQBRoUl2ITtUAlGqYMNPoXW5Qt4dA6eGhUQaDgbrlhlcJoiXSxGj7yca15TvBW" +
                "-AoIXWeH6k6Q0Rcj9z7uJP4N2QxXKGilpHCewvCa-yiH3c-lWOCviW00YsxvG17TLPqBwRmxUjYXbZ8Z-Va8WVhK6N-J2kS" +
                "kJgQ0wkxe6eRi7ntcKQGYSgH_Csj2ghb1VuEzQqLOPkPsjjQUBICqp-C7f4fRJAvY48UJFG3U4k6mG4h5wdN5jXabXzgfAQ" +
                "sUg2YjeIha7ZoSYaEuIapu4wqAELyfobSAmvzmZg";

        InetSocketAddress address = new InetSocketAddress(8000);
        HttpServer httpServer = null;

        try {
            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpHandler handler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "Payload doesn't matter".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/oauth2/revoke", handler);

        httpServer.start();

        boolean result = ServiceProviderUtil.revokeAccessToken("http://localhost:8000", validAccessToken);

        httpServer.stop(0);

        Assert.assertTrue(result);
    }

    @Test
    public void testRevokeTokenWithWrongResponse() {
        String validAccessToken = "eyJ4NXQiOiJPV1JpTXpaaVlURXhZVEl4WkdGa05UVTJOVE0zTWpkaFltTmxNVFZrTnpRMU56a3paV" +
                "Gc1TVRrNE0yWmxOMkZoWkdaalpURmlNemxsTTJJM1l6ZzJNZyIsImtpZCI6Ik9XUmlNelppWVRFeFlUSXhaR0ZrTlRVMk5U" +
                "TTNNamRoWW1ObE1UVmtOelExTnprelpUZzVNVGs0TTJabE4yRmhaR1pqWlRGaU16bGxNMkkzWXpnMk1nX1JTMjU2IiwidHl" +
                "wIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJuTVpVbUEyaGxmYnNRVnZmb293YjR1SVFsMmdhIiwiYXV0Ijo" +
                "iQVBQTElDQVRJT04iLCJiaW5kaW5nX3R5cGUiOiJjZXJ0aWZpY2F0ZSIsImlzcyI6Imh0dHBzOlwvXC9sb2NhbGhvc3Q6OT" +
                "Q0Nlwvb2F1dGgyXC90b2tlbiIsImNsaWVudF9pZCI6Im5NWlVtQTJobGZic1FWdmZvb3diNHVJUWwyZ2EiLCJhdWQiOiJuT" +
                "VpVbUEyaGxmYnNRVnZmb293YjR1SVFsMmdhIiwibmJmIjoxNzIzNDQxODQxLCJhenAiOiJuTVpVbUEyaGxmYnNRVnZmb293" +
                "YjR1SVFsMmdhIiwib3JnX2lkIjoiMTAwODRhOGQtMTEzZi00MjExLWEwZDUtZWZlMzZiMDgyMjExIiwiY25mIjp7Ing1dCN" +
                "TMjU2IjoiODA3LUU4S2dVTVY2ZFJIVFFpMV9RWW81ZXlQdmptamJ4Q3R1bmJGaXhWMCJ9LCJleHAiOjE3MjM0NDU0NDEsIm" +
                "9yZ19uYW1lIjoiU3VwZXIiLCJpYXQiOjE3MjM0NDE4NDEsImJpbmRpbmdfcmVmIjoiYWU2YmM4NzE1NGRhMjEwYTdjM2FjN" +
                "2I1ZjlhZDVjNjciLCJqdGkiOiIwNmViNTc2ZS02MzQ0LTRmYTMtODNiYy04MDEzZGZkYWVlOGYifQ.Vnu6mUjlqRFfyLFhg" +
                "JFvLqGvxFiegWlNF-bt7fXg6feZLCEqp5kQBRoUl2ITtUAlGqYMNPoXW5Qt4dA6eGhUQaDgbrlhlcJoiXSxGj7yca15TvBW" +
                "-AoIXWeH6k6Q0Rcj9z7uJP4N2QxXKGilpHCewvCa-yiH3c-lWOCviW00YsxvG17TLPqBwRmxUjYXbZ8Z-Va8WVhK6N-J2kS" +
                "kJgQ0wkxe6eRi7ntcKQGYSgH_Csj2ghb1VuEzQqLOPkPsjjQUBICqp-C7f4fRJAvY48UJFG3U4k6mG4h5wdN5jXabXzgfAQ" +
                "sUg2YjeIha7ZoSYaEuIapu4wqAELyfobSAmvzmZg";

        InetSocketAddress address = new InetSocketAddress(8000);
        HttpServer httpServer = null;

        try {
            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpHandler handler = new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = "Payload doesn't matter".getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST,
                        response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        };

        httpServer.createContext("/oauth2/revoke", handler);

        httpServer.start();

        boolean result = ServiceProviderUtil.revokeAccessToken("http://localhost:8000", validAccessToken);

        httpServer.stop(0);

        Assert.assertFalse(result);
    }


    @Test
    public void testRevokeTokenWithInvalidToken() {
        String validAccessToken = "Invalid Token";

        boolean result = ServiceProviderUtil.revokeAccessToken("http://localhost:8000", validAccessToken);

        Assert.assertFalse(result);
    }

    @Test
    public void testRevokeTokenWithInvalidURL() {
        String validAccessToken = "eyJ4NXQiOiJPV1JpTXpaaVlURXhZVEl4WkdGa05UVTJOVE0zTWpkaFltTmxNVFZrTnpRMU56a3paV" +
                "Gc1TVRrNE0yWmxOMkZoWkdaalpURmlNemxsTTJJM1l6ZzJNZyIsImtpZCI6Ik9XUmlNelppWVRFeFlUSXhaR0ZrTlRVMk5U" +
                "TTNNamRoWW1ObE1UVmtOelExTnprelpUZzVNVGs0TTJabE4yRmhaR1pqWlRGaU16bGxNMkkzWXpnMk1nX1JTMjU2IiwidHl" +
                "wIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJuTVpVbUEyaGxmYnNRVnZmb293YjR1SVFsMmdhIiwiYXV0Ijo" +
                "iQVBQTElDQVRJT04iLCJiaW5kaW5nX3R5cGUiOiJjZXJ0aWZpY2F0ZSIsImlzcyI6Imh0dHBzOlwvXC9sb2NhbGhvc3Q6OT" +
                "Q0Nlwvb2F1dGgyXC90b2tlbiIsImNsaWVudF9pZCI6Im5NWlVtQTJobGZic1FWdmZvb3diNHVJUWwyZ2EiLCJhdWQiOiJuT" +
                "VpVbUEyaGxmYnNRVnZmb293YjR1SVFsMmdhIiwibmJmIjoxNzIzNDQxODQxLCJhenAiOiJuTVpVbUEyaGxmYnNRVnZmb293" +
                "YjR1SVFsMmdhIiwib3JnX2lkIjoiMTAwODRhOGQtMTEzZi00MjExLWEwZDUtZWZlMzZiMDgyMjExIiwiY25mIjp7Ing1dCN" +
                "TMjU2IjoiODA3LUU4S2dVTVY2ZFJIVFFpMV9RWW81ZXlQdmptamJ4Q3R1bmJGaXhWMCJ9LCJleHAiOjE3MjM0NDU0NDEsIm" +
                "9yZ19uYW1lIjoiU3VwZXIiLCJpYXQiOjE3MjM0NDE4NDEsImJpbmRpbmdfcmVmIjoiYWU2YmM4NzE1NGRhMjEwYTdjM2FjN" +
                "2I1ZjlhZDVjNjciLCJqdGkiOiIwNmViNTc2ZS02MzQ0LTRmYTMtODNiYy04MDEzZGZkYWVlOGYifQ.Vnu6mUjlqRFfyLFhg" +
                "JFvLqGvxFiegWlNF-bt7fXg6feZLCEqp5kQBRoUl2ITtUAlGqYMNPoXW5Qt4dA6eGhUQaDgbrlhlcJoiXSxGj7yca15TvBW" +
                "-AoIXWeH6k6Q0Rcj9z7uJP4N2QxXKGilpHCewvCa-yiH3c-lWOCviW00YsxvG17TLPqBwRmxUjYXbZ8Z-Va8WVhK6N-J2kS" +
                "kJgQ0wkxe6eRi7ntcKQGYSgH_Csj2ghb1VuEzQqLOPkPsjjQUBICqp-C7f4fRJAvY48UJFG3U4k6mG4h5wdN5jXabXzgfAQ" +
                "sUg2YjeIha7ZoSYaEuIapu4wqAELyfobSAmvzmZg";

        boolean result = ServiceProviderUtil.revokeAccessToken("http://localhost:8", validAccessToken);

        Assert.assertFalse(result);
    }

    @Test
    public void testRevokeTokenWithoutClientId() {
        String validAccessToken =
                "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRta" +
                "W4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.jKT31t8RseZLeSDtN-4t5_IG33J-SzoRChoQq8OwdRspElX0_sM4b6labjjsL8Ht" +
                "hNRq8EEx0l3USd6mgTB0bFNCafGEGEnbgOVuK74n8gdCJCpI20_5osXNWxifdbSWTCFNiY0THNzm5C8Po2BSN5s2PN3eHqXmK72" +
                "kJnWOStVHTWwGbagbPsR3_NKe6DmlTN0VA-TeH0VL9sY7-4rApzPtTv7YtMJIAUebG9zha47piNY2W1bQkTE98FVevy-0r1oeC3" +
                "PqesjwlIfSbCBFutsfjUVsOe7nAWJIKIHaxfFNU2IXWbvniSfrnot_I1CELQ29GC9bNnRinPKxHnPvNQ";

        boolean result = ServiceProviderUtil.revokeAccessToken("http://localhost:8", validAccessToken);

        Assert.assertFalse(result);
    }


    @Test
    public void testConvertOBClientRegistrationRequest1JsonStringToISDCRPayload() {
        String obClientRegistrationRequest1JsonString = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[" +
                    "\"authorization_code\"," +
                    "\"client_credentials\"," +
                    "\"refresh_token\"" +
                "]," +
                "\"application_type\":\"web\"," +
                "\"iss\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"redirect_uris\":[" +
                    "\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"" +
                "]," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"aud\":\"https:\\/\\/localbank.com\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5e" +
                "E1KUk92ayJ9.eyJpc3MiOiJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2" +
                "FyZV9tb2RlIjoiVGVzdCIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9p" +
                "ZCI6ImpGUXVRNGVRYk5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUD" +
                "IgKFNhbmRib3gpIiwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRl" +
                "ZCBmb3IgdGVzdGluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaH" +
                "R0cHM6Ly93c28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRp" +
                "cmVjdF91cmlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcy" +
                "I6WyJBSVNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRo" +
                "b3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOi" +
                "JBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNC" +
                "UElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YX" +
                "RlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93" +
                "c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcm" +
                "dfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJz" +
                "YWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2" +
                "luZXNzIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNz" +
                "In1dLCJvcmdfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMD" +
                "AxSFFRclpBQVgvMDAxNTgwMDAwMUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9r" +
                "ZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIUVFyWk" +
                "FBWC5qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51" +
                "ay8wMDE1ODAwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2" +
                "VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZv" +
                "a2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91cmkiOiJodHRwczovL3dzbzIuY29tIi" +
                "wic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZV9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMiBP" +
                "cGVuIEJhbmtpbmciLCJqdGkiOiJyZGJqNXV6ajhsb3oiLCJpYXQiOjE3MjMwMjgxMjQsImV4cCI6MTcyMzAzMTcyNH0.glofOD" +
                "eYIlxGd7NWYa9GAqvtUBFqq0ta0Lp1yghRLbg_UKWnbrilwzCqJ9WVJXrXL9m0q0EFMjaTjbP6nBPn4pcGxgzS6EP8MGgj2YuF" +
                "HabsuLHSRo90oGTd4xSlc4iFtxD0iGtKAKRwyGtCg2UzfL07vjYft91Z9PUel6B5jWGkIEa00cOv-zkl0_9GTH6MA2i7xBI9-D" +
                "hZcpF7jPq4i4GxPryMYo1K32XewUscsgnU5iXumCG-NQ06B_djyXL-qWjTlvkhTZa44xqVKUfVicIRHER37S43CFwMWfqOelQo" +
                "ss5La9RtHi2zsPyRRdFCXBalqHNAy-osgzrYh_oKPA\"," +
                "\"scope\":\"accounts payments\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"exp\":1723031724," +
                "\"iat\":1723028124," +
                "\"jti\":\"tt07q1kcx7v1\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";

        String result = ServiceProviderUtil.convertOBClientRegistrationRequest1JsonStringToISDCRPayload(
                obClientRegistrationRequest1JsonString
        );

        String expected = "{" +
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
                "mNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2ludCI6Imh0dHBz" +
                "Oi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvMDAxNTgwMDAwMUhRUXJaQUFYLmp" +
                "3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLz" +
                "AwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIUVFyWkFBWC5qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2lud" +
                "CI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNN" +
                "U3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmF" +
                "ua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsIn" +
                "NvZnR3YXJlX3BvbGljeV91cmkiOiJodHRwczovL3dzbzIuY29tIiwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb" +
                "20iLCJzb2Z0d2FyZV9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiJyZGJqNXV6ajhsb3oiLCJp" +
                "YXQiOjE3MjMwMjgxMjQsImV4cCI6MTcyMzAzMTcyNH0.glofODeYIlxGd7NWYa9GAqvtUBFqq0ta0Lp1yghRLbg_UKWnbrilwzC" +
                "qJ9WVJXrXL9m0q0EFMjaTjbP6nBPn4pcGxgzS6EP8MGgj2YuFHabsuLHSRo90oGTd4xSlc4iFtxD0iGtKAKRwyGtCg2UzfL07vj" +
                "Yft91Z9PUel6B5jWGkIEa00cOv-zkl0_9GTH6MA2i7xBI9-DhZcpF7jPq4i4GxPryMYo1K32XewUscsgnU5iXumCG-NQ06B_djy" +
                "XL-qWjTlvkhTZa44xqVKUfVicIRHER37S43CFwMWfqOelQoss5La9RtHi2zsPyRRdFCXBalqHNAy-osgzrYh_oKPA\"," +
                "\"jwks_uri\":" +
                "\"https:\\/\\/keystore.openbankingtest.org.uk\\/0015800001HQQrZAAX\\/jFQuQ4eQbNCMSqdCog21nF.jwks\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"tls_client_certificate_bound_access_tokens\":true," +
                "\"client_name\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";
        Assert.assertEquals(result, expected);

    }

    @Test
    public void testConvertISDCRResponseJsonStringToOBClientRegistrationResponse1() throws ParseException {
        String expected = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":null," +
                "\"backchannel_user_code_parameter_supported\":false," +
                "\"tls_client_auth_subject_dn\":null," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"client_id\":\"dl5fwOopGZNd9LVbf3jhVvsNWfka\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3" +
                "MiOiJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjo" +
                "iVGVzdCIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpG" +
                "UXVRNGVRYk5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgK" +
                "FNhbmRib3gpIiwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYX" +
                "RlZCBmb3IgdGVzdGluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJ" +
                "pIjoiaHR0cHM6Ly93c28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0" +
                "d2FyZV9yZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb" +
                "2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaX" +
                "R5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDA" +
                "xSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJy" +
                "b2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ" +
                "0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX" +
                "0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmU" +
                "iLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2Nv" +
                "bnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0N" +
                "zc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzIiwiZW1haWwiOiJzYWNoaW5pc0B3c2" +
                "8yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2ludCI" +
                "6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvMDAxNTgwMDAw" +
                "MUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua" +
                "2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIUVFyWkFBWC5qd2tzIiwic2" +
                "9mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODA" +
                "wMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2VuZHBv" +
                "aW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva" +
                "2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91cmkiOiJodHRwczovL3dzbzIuY2" +
                "9tIiwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZV9vbl9iZWhhbGZfb2Zfb3JnIjo" +
                "iV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiI4NzF1ZXBvOHRrMWciLCJpYXQiOjE3MjMwOTU0MTksImV4cCI6MTcyMzA5" +
                "OTAxOX0.De08Y72CMdVxXpYJscs0JhxK8lIjxa-h61kLRFuVs-IRnLszbBqYu2ShLsycRRmwxgImHswqOeEYn1Rp8kwGP" +
                "v1IlAh_XXGmPvd-DXWW44P_y6bufP0_PMjrUpTweVcXi-CNY0yttLQMWL1eIE1vwtnOLLdXjCBRP9bRlk9lj69OABFbMx" +
                "jsIwV8f6_qIOLlLKQtPIKrdi5agwsgU8nrsxHVa-4IdSwwxNmrzF9zm_ULxlsPelbxTKM0QIx0-g7Lj_iWzqnmv9eE2jt" +
                "L-ZT7BQx1o0IY17DGWNbCHntV9pVEOxNH1gsg9SxmUVaK9Nhg1hcRwwXRffE7Y3hy8Gmzsw\"," +
                "\"client_secret_expires_at\":0," +
                "\"backchannel_client_notification_endpoint\":null," +
                "\"scope\":null," +
                "\"client_secret\":\"17GMCmJxTfoJyVuUSfN6QrJhI3eQXr9EH_Wu2qtVolEa\"," +
                "\"client_id_issued_at\":1723095426," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"backchannel_authentication_request_signing_alg\":null," +
                "\"backchannel_token_delivery_mode\":\"poll\"," +
                "\"response_types\":[]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";

        String spServiceProviderResponse = "{" +
                "\"client_name\": \"jFQuQ4eQbNCMSqdCog21nF\"" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":null," +
                "\"backchannel_user_code_parameter_supported\":false," +
                "\"tls_client_auth_subject_dn\":null," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"client_id\":\"dl5fwOopGZNd9LVbf3jhVvsNWfka\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":" +
                "\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc" +
                "3MiOiJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlI" +
                "joiVGVzdCIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6I" +
                "mpGUXVRNGVRYk5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQU" +
                "DIgKFNhbmRib3gpIiwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY" +
                "3JlYXRlZCBmb3IgdGVzdGluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllb" +
                "nRfdXJpIjoiaHR0cHM6Ly93c28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciL" +
                "CJzb2Z0d2FyZV9yZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0M" +
                "SJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfY" +
                "XV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wM" +
                "DE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlI" +
                "joiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6W" +
                "yJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsI" +
                "kNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0d" +
                "XMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JV" +
                "EVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsI" +
                "nBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzIiwiZW1haWwiO" +
                "iJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfa" +
                "ndrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRc" +
                "lpBQVgvMDAxNTgwMDAwMUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZ" +
                "XlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIU" +
                "VFyWkFBWC5qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0Z" +
                "XN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfa" +
                "ndrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4M" +
                "DAwMDFIUVFyWkFBWC9yZXZva2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91c" +
                "mkiOiJodHRwczovL3dzbzIuY29tIiwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZ" +
                "V9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiI4NzF1ZXBvOHRrMWciLCJpYXQiOjE3M" +
                "jMwOTU0MTksImV4cCI6MTcyMzA5OTAxOX0.De08Y72CMdVxXpYJscs0JhxK8lIjxa-h61kLRFuVs-IRnLszbBqYu2ShL" +
                "sycRRmwxgImHswqOeEYn1Rp8kwGPv1IlAh_XXGmPvd-DXWW44P_y6bufP0_PMjrUpTweVcXi-CNY0yttLQMWL1eIE1vw" +
                "tnOLLdXjCBRP9bRlk9lj69OABFbMxjsIwV8f6_qIOLlLKQtPIKrdi5agwsgU8nrsxHVa-4IdSwwxNmrzF9zm_ULxlsPe" +
                "lbxTKM0QIx0-g7Lj_iWzqnmv9eE2jtL-ZT7BQx1o0IY17DGWNbCHntV9pVEOxNH1gsg9SxmUVaK9Nhg1hcRwwXRffE7Y" +
                "3hy8Gmzsw\"," +
                "\"client_secret_expires_at\":0," +
                "\"backchannel_client_notification_endpoint\":null," +
                "\"scope\":null," +
                "\"client_secret\":\"17GMCmJxTfoJyVuUSfN6QrJhI3eQXr9EH_Wu2qtVolEa\"," +
                "\"client_id_issued_at\":1723095426," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"backchannel_authentication_request_signing_alg\":null," +
                "\"backchannel_token_delivery_mode\":\"poll\"," +
                "\"response_types\":[]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";
        JSONObject expectedJsonObject = (JSONObject) (new JSONParser()).parse(expected);

        String obClientRegistrationResponse1 = ServiceProviderUtil
                .convertISDCRResponseJsonStringToOBClientRegistrationResponse1(
                        spServiceProviderResponse
                );



        JSONObject actualJsonObject = (JSONObject) (new JSONParser()).parse(obClientRegistrationResponse1);

        actualJsonObject.remove("client_id_issued_at");
        expectedJsonObject.remove("client_id_issued_at");

        Assert.assertEquals(actualJsonObject, expectedJsonObject);
    }
}
