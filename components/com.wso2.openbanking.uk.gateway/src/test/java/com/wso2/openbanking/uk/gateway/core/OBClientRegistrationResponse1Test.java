package com.wso2.openbanking.uk.gateway.core;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests the OBClientRegistrationResponse1.
 */
public class OBClientRegistrationResponse1Test {
    @Test
    public void testGetSpServiceProviderResponse() throws ParseException {
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

        OBClientRegistrationResponse1 obClientRegistrationResponse1 =
                new OBClientRegistrationResponse1(spServiceProviderResponse);
        String actual = obClientRegistrationResponse1.getSpServiceProviderResponse();

        JSONObject actualJsonObject = (JSONObject) (new JSONParser()).parse(actual);
        Assert.assertEquals(actualJsonObject, expectedJsonObject);
    }


}
