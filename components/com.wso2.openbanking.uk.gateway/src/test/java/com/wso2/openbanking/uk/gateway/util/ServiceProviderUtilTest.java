package com.wso2.openbanking.uk.gateway.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests the ServiceProviderUtil.
 */
public class ServiceProviderUtilTest {
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
}
