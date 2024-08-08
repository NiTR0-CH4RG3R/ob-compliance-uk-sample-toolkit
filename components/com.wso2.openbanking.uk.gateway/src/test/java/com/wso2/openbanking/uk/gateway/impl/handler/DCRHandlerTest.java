package com.wso2.openbanking.uk.gateway.impl.handler;

import com.wso2.openbanking.uk.gateway.exception.OpenBankingAPIHandlerException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ResponseContextDTO;
import org.wso2.carbon.apimgt.common.gateway.extensionlistener.PayloadHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * This class tests the DCRHandler.
 */
public class DCRHandlerTest {
    DCRHandler dcrHandler = null;

    @BeforeClass
    public void setUp() {
        dcrHandler = new DCRHandler();
    }

    @Test
    public void testCanProcess() {
        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        boolean canProcess = dcrHandler.canProcess(msgInfoDTO, null);
        Assert.assertTrue(canProcess);
    }

    @Test
    public void testPreProcessRequest() throws OpenBankingAPIHandlerException {
        String modifiedPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[" +
                "\"authorization_code\"," +
                "\"client_credentials\"," +
                "\"refresh_token\"" +
                "]," +
                "\"application_type\":\"web\"," +
                "\"iss\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"redirect_uris\":" +
                "[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"aud\":\"https:\\/\\/localbank.com\"," +
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
                "V9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiJld3UwMDUwdjl2MWYiLCJpYXQiOjE3M" +
                "jMwODgyMzMsImV4cCI6MTcyMzA5MTgzM30.h7d3b-z6X0D_Z2ZC8R1G3DOUvZRpNgYBLEuyeUUuM6Bw7XROCts_75InX" +
                "xUjwUTum5Tnm-PVr0A0szq3Wyx_XG39SdvQJ2aTqgGYqpTEenatf9sijlHqAzG_cYDXQafo4cgWkEF-UxlMj_KDVf_Hl" +
                "ws5ByVp_pPlQUWr5EG8fHuwDZpMl1rjMUlA1LHR07KI1sJInKba-cic4LBwU2X287baML-ZbWNodLa5xosiJBaSCp-xs" +
                "o9AfP_8YmqZVv4LEVCFqtQ406lb67X0gt9wybU7mqZD2KIvfYH_hW_rm9nXYiig0zq2daMom9dyTC3RDakSsOyzOkSr4" +
                "167zSYe8A\"," +
                "\"scope\":\"accounts payments\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"exp\":1723091833," +
                "\"iat\":1723088233," +
                "\"jti\":\"l0u5q6ie24x1\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";
        String inputPayload =
                "<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><text xmlns=\"http://" +
                        "ws.apache.org/commons/ns/payload\">eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhT" +
                        "X1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3MiOiJqRlF1UTRlUWJOQ01TcWRDb2cyMW5GIiwiYXVkIjoiaH" +
                        "R0cHM6Ly9sb2NhbGJhbmsuY29tIiwic2NvcGUiOiJhY2NvdW50cyBwYXltZW50cyIsInRva2VuX2VuZHBvaW50X" +
                        "2F1dGhfbWV0aG9kIjoicHJpdmF0ZV9rZXlfand0IiwidG9rZW5fZW5kcG9pbnRfYXV0aF9zaWduaW5nX2FsZyI6" +
                        "IlBTMjU2IiwiZ3JhbnRfdHlwZXMiOlsiYXV0aG9yaXphdGlvbl9jb2RlIiwiY2xpZW50X2NyZWRlbnRpYWxzIiw" +
                        "icmVmcmVzaF90b2tlbiJdLCJyZXNwb25zZV90eXBlcyI6WyJjb2RlIGlkX3Rva2VuIl0sImlkX3Rva2VuX3NpZ2" +
                        "5lZF9yZXNwb25zZV9hbGciOiJQUzI1NiIsInJlcXVlc3Rfb2JqZWN0X3NpZ25pbmdfYWxnIjoiUFMyNTYiLCJhc" +
                        "HBsaWNhdGlvbl90eXBlIjoid2ViIiwic29mdHdhcmVfaWQiOiJqRlF1UTRlUWJOQ01TcWRDb2cyMW5GIiwicmVk" +
                        "aXJlY3RfdXJpcyI6WyJodHRwczovL3d3dy5nb29nbGUuY29tL3JlZGlyZWN0cy9yZWRpcmVjdDEiXSwianRpIjo" +
                        "iZG8xMmlmOHUzaWNuIiwiaWF0IjoxNzIzMDg4MDg3LCJleHAiOjE3MjMwOTE2ODcsInNvZnR3YXJlX3N0YXRlbW" +
                        "VudCI6ImV5SmhiR2NpT2lKUVV6STFOaUlzSW5SNWNDSTZJa3BYVkNJc0ltdHBaQ0k2SWpkbFNqaFRYMXBuZG14W" +
                        "mVFWkJSbE5uYUZZNWVFMUtVazkyYXlKOS5leUpwYzNNaU9pSlBjR1Z1UW1GdWEybHVaeUJNZEdRaUxDSnpiMlow" +
                        "ZDJGeVpWOWxiblpwY205dWJXVnVkQ0k2SW5OaGJtUmliM2dpTENKemIyWjBkMkZ5WlY5dGIyUmxJam9pVkdWemR" +
                        "DSXNJbk52Wm5SM1lYSmxYMmxrSWpvaWFrWlJkVkUwWlZGaVRrTk5VM0ZrUTI5bk1qRnVSaUlzSW5OdlpuUjNZWE" +
                        "psWDJOc2FXVnVkRjlwWkNJNkltcEdVWFZSTkdWUllrNURUVk54WkVOdlp6SXhia1lpTENKemIyWjBkMkZ5WlY5a" +
                        "mJHbGxiblJmYm1GdFpTSTZJbGRUVHpJZ1QzQmxiaUJDWVc1cmFXNW5JRlJRVURJZ0tGTmhibVJpYjNncElpd2lj" +
                        "MjltZEhkaGNtVmZZMnhwWlc1MFgyUmxjMk55YVhCMGFXOXVJam9pVkdocGN5QmhiSFJsY201aGRHbDJaU0JVVUZ" +
                        "BZ2FYTWdZM0psWVhSbFpDQm1iM0lnZEdWemRHbHVaeUJ3ZFhKd2IzTmxjeTRnSWl3aWMyOW1kSGRoY21WZmRtVn" +
                        "ljMmx2YmlJNk1TNDFMQ0p6YjJaMGQyRnlaVjlqYkdsbGJuUmZkWEpwSWpvaWFIUjBjSE02THk5M2MyOHlMbU52Y" +
                        "lNJc0lteHZaMjlmZFhKcElqb2lhSFIwY0hNNkx5OTNkM2N1ZDNOdk1pNWpiMjB2ZDNOdk1pNXFjR2NpTENKemIy" +
                        "WjBkMkZ5WlY5eVpXUnBjbVZqZEY5MWNtbHpJanBiSW1oMGRIQnpPaTh2ZDNkM0xtZHZiMmRzWlM1amIyMHZjbVZ" +
                        "rYVhKbFkzUnpMM0psWkdseVpXTjBNU0pkTENKemIyWjBkMkZ5WlY5eWIyeGxjeUk2V3lKQlNWTlFJaXdpVUVsVF" +
                        "VDSXNJa05DVUVsSklsMHNJbTl5WjJGdWFYTmhkR2x2Ymw5amIyMXdaWFJsYm5SZllYVjBhRzl5YVhSNVgyTnNZV" +
                        "2x0Y3lJNmV5SmhkWFJvYjNKcGRIbGZhV1FpT2lKUFFrZENVaUlzSW5KbFoybHpkSEpoZEdsdmJsOXBaQ0k2SWxW" +
                        "dWEyNXZkMjR3TURFMU9EQXdNREF4U0ZGUmNscEJRVmdpTENKemRHRjBkWE1pT2lKQlkzUnBkbVVpTENKaGRYUm9" +
                        "iM0pwYzJGMGFXOXVjeUk2VzNzaWJXVnRZbVZ5WDNOMFlYUmxJam9pUjBJaUxDSnliMnhsY3lJNld5SlFTVk5RSW" +
                        "l3aVFVbFRVQ0lzSWtOQ1VFbEpJbDE5TEhzaWJXVnRZbVZ5WDNOMFlYUmxJam9pU1VVaUxDSnliMnhsY3lJNld5S" +
                        "lFTVk5RSWl3aVEwSlFTVWtpTENKQlNWTlFJbDE5TEhzaWJXVnRZbVZ5WDNOMFlYUmxJam9pVGt3aUxDSnliMnhs" +
                        "Y3lJNld5SlFTVk5RSWl3aVFVbFRVQ0lzSWtOQ1VFbEpJbDE5WFgwc0luTnZablIzWVhKbFgyeHZaMjlmZFhKcEl" +
                        "qb2lhSFIwY0hNNkx5OTNjMjh5TG1OdmJTOTNjMjh5TG1wd1p5SXNJbTl5WjE5emRHRjBkWE1pT2lKQlkzUnBkbV" +
                        "VpTENKdmNtZGZhV1FpT2lJd01ERTFPREF3TURBeFNGRlJjbHBCUVZnaUxDSnZjbWRmYm1GdFpTSTZJbGRUVHpJZ" +
                        "0tGVkxLU0JNU1UxSlZFVkVJaXdpYjNKblgyTnZiblJoWTNSeklqcGJleUp1WVcxbElqb2lWR1ZqYUc1cFkyRnNJ" +
                        "aXdpWlcxaGFXd2lPaUp6WVdOb2FXNXBjMEIzYzI4eUxtTnZiU0lzSW5Cb2IyNWxJam9pS3prME56YzBNamMwTXp" +
                        "jMElpd2lkSGx3WlNJNklsUmxZMmh1YVdOaGJDSjlMSHNpYm1GdFpTSTZJa0oxYzJsdVpYTnpJaXdpWlcxaGFXd2" +
                        "lPaUp6WVdOb2FXNXBjMEIzYzI4eUxtTnZiU0lzSW5Cb2IyNWxJam9pS3prME56YzBNamMwTXpjMElpd2lkSGx3W" +
                        "lNJNklrSjFjMmx1WlhOekluMWRMQ0p2Y21kZmFuZHJjMTlsYm1Sd2IybHVkQ0k2SW1oMGRIQnpPaTh2YTJWNWMz" +
                        "UnZjbVV1YjNCbGJtSmhibXRwYm1kMFpYTjBMbTl5Wnk1MWF5OHdNREUxT0RBd01EQXhTRkZSY2xwQlFWZ3ZNREF" +
                        "4TlRnd01EQXdNVWhSVVhKYVFVRllMbXAzYTNNaUxDSnZjbWRmYW5kcmMxOXlaWFp2YTJWa1gyVnVaSEJ2YVc1ME" +
                        "lqb2lhSFIwY0hNNkx5OXJaWGx6ZEc5eVpTNXZjR1Z1WW1GdWEybHVaM1JsYzNRdWIzSm5MblZyTHpBd01UVTRNR" +
                        "EF3TURGSVVWRnlXa0ZCV0M5eVpYWnZhMlZrTHpBd01UVTRNREF3TURGSVVWRnlXa0ZCV0M1cWQydHpJaXdpYzI5" +
                        "bWRIZGhjbVZmYW5kcmMxOWxibVJ3YjJsdWRDSTZJbWgwZEhCek9pOHZhMlY1YzNSdmNtVXViM0JsYm1KaGJtdHB" +
                        "ibWQwWlhOMExtOXlaeTUxYXk4d01ERTFPREF3TURBeFNGRlJjbHBCUVZndmFrWlJkVkUwWlZGaVRrTk5VM0ZrUT" +
                        "I5bk1qRnVSaTVxZDJ0eklpd2ljMjltZEhkaGNtVmZhbmRyYzE5eVpYWnZhMlZrWDJWdVpIQnZhVzUwSWpvaWFIU" +
                        "jBjSE02THk5clpYbHpkRzl5WlM1dmNHVnVZbUZ1YTJsdVozUmxjM1F1YjNKbkxuVnJMekF3TVRVNE1EQXdNREZJ" +
                        "VVZGeVdrRkJXQzl5WlhadmEyVmtMMnBHVVhWUk5HVlJZazVEVFZOeFpFTnZaekl4YmtZdWFuZHJjeUlzSW5Odlp" +
                        "uUjNZWEpsWDNCdmJHbGplVjkxY21raU9pSm9kSFJ3Y3pvdkwzZHpiekl1WTI5dElpd2ljMjltZEhkaGNtVmZkRz" +
                        "l6WDNWeWFTSTZJbWgwZEhCek9pOHZkM052TWk1amIyMGlMQ0p6YjJaMGQyRnlaVjl2Ymw5aVpXaGhiR1pmYjJaZ" +
                        "mIzSm5Jam9pVjFOUE1pQlBjR1Z1SUVKaGJtdHBibWNpTENKcWRHa2lPaUp0WW1waWVIaHpaV3B4YW5BaUxDSnBZ" +
                        "WFFpT2pFM01qTXdPRGd3T0Rjc0ltVjRjQ0k2TVRjeU16QTVNVFk0TjMwLk9NVVBabXM2SF9tQlpLbEdGNUVMVEg" +
                        "tcWJFQ21JeUlqUkxTZng4NHVrZFdQRUM1dlJXOGZYYWUtRHJxdHp3ZFBEOHBBbE90aWRaX24tU1c0dmdqTlpRTz" +
                        "V1QTRDME9oRWlrVlhTV2xfdDA5aDNVSkZEMUc3eU5BbFNlcGlPTUpSZThKSzZKcmVSczFMRENadllCMnFqTmVEO" +
                        "U9zSzF2T1NJcm1EZ1E3cUcwTUJiaFdfTWwzUTlXMHlUU3pIaHF6N0I0OHBMR2dzakF0dkZadm1oWW1SbnhJRjN6" +
                        "ZWswWjVkZWl5YU96dGJUZXNBTnpNUXZrMm54amRNb3RGMWJ4TS1mUTJJdkl1aWdEdV9xTHFpYWZZUDVzeC1hQTR" +
                        "LbG00eGlQdE5VSldiMXcyRVF1R0tNMW42Z1NSLTRJNUo4U2s4bnBJaFIzZ3dPOExzLURCNmoyNmdTdyJ9.lK0Zx" +
                        "SZzDX4gNSrZrEUHhjKpe_YWjaJqACMXQb71r-X5HEJA7XKZ0bzGjgLfnHY68C3G_4Z7SMbLTiLJU5FefUQG6nkY" +
                        "Z7nwQGL4pil3gLOjw6JqBz-ICrzoC5qYrLnee8Mv_gkzotgxKZmhEPk8yNddv8ktY4RR_xZ1rp7VhNTqEiOLFsn" +
                        "vPbopSJ2igx-b0A0yPcBtKgNo2qfLR5FZ-dskuX2xD0a2H5eqL2T1Cp8jYzG2Rofw5D5eatfhS9lX2Sjq8kNVye" +
                        "RESbQy7F3P9rakNajTsLZOP20sT51Wwwkqpe1C02bQulLWIT9wwEkCsV6-H1c437E14T0Ms9OOyQ</text></so" +
                        "apenv:Body>";

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(
                new HashMap<>() { {
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
        Assert.assertEquals(extensionResponseDTO.getStatusCode(), 200);
        Assert.assertEquals(extensionResponseDTO.getHeaders().get("Content-Type"), "application/json");
        Assert.assertEquals(extensionResponseDTO.getPayload(),
                new ByteArrayInputStream(modifiedPayload.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testPostProcessRequest() throws OpenBankingAPIHandlerException {
        String modifiedPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":\"web\"," +
                "\"redirect_uris\":[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"token_type_extension\":\"JWT\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
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
                "cGVuIEJhbmtpbmciLCJqdGkiOiJwZnhjZ2Q5cjc1bm4iLCJpYXQiOjE3MjMwODg2MzcsImV4cCI6MTcyMzA5MjIzN30.X5CCAf" +
                "4UsV6dRibVimFIDzMxBou7qHSr-GqiJuDZDxiO3RmrXO8GBvmP_sLDzut8Wa4ALA1eYVvxkHDuqI_IZp7m2rLxvzNG30llty9b" +
                "TnDnst190TxkqhAqs8BJaPp_TdvYlrloNabr_NZD9wC-k-v20JggK8rKLp3Eg-iuQWp7aJIKe6f02DqY0rapcQzpSDkk2MUbJF" +
                "sr5v1R3P2TupORM_I0cPJOgJcuFYi0vvCp-xNNk6EDQeysDGahTL5FWFH0g8g45CwmXnnWm7Z3ExjlMf5gYGja_kDsA9hCn-nO" +
                "sHyzTzNdSMURYvNsWGqmBV2t8c8dBwzX53PASeXDFw\"," +
                "\"jwks_uri\":\"https:\\/\\/keystore.openbankingtest.org.uk\\/0015800001HQQrZAAX\\/jFQuQ4eQbNCMSqdCo" +
                "g21nF.jwks\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"tls_client_certificate_bound_access_tokens\":true," +
                "\"client_name\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";
        String inputPayload = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[" +
                "\"authorization_code\"," +
                "\"client_credentials\"," +
                "\"refresh_token\"" +
                "]," +
                "\"application_type\":\"web\"," +
                "\"iss\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"redirect_uris\":" +
                "[\"https:\\/\\/www.google.com\\/redirects\\/redirect1\"]," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"aud\":\"https:\\/\\/localbank.com\"," +
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
                "V9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiJld3UwMDUwdjl2MWYiLCJpYXQiOjE3M" +
                "jMwODgyMzMsImV4cCI6MTcyMzA5MTgzM30.h7d3b-z6X0D_Z2ZC8R1G3DOUvZRpNgYBLEuyeUUuM6Bw7XROCts_75InX" +
                "xUjwUTum5Tnm-PVr0A0szq3Wyx_XG39SdvQJ2aTqgGYqpTEenatf9sijlHqAzG_cYDXQafo4cgWkEF-UxlMj_KDVf_Hl" +
                "ws5ByVp_pPlQUWr5EG8fHuwDZpMl1rjMUlA1LHR07KI1sJInKba-cic4LBwU2X287baML-ZbWNodLa5xosiJBaSCp-xs" +
                "o9AfP_8YmqZVv4LEVCFqtQ406lb67X0gt9wybU7mqZD2KIvfYH_hW_rm9nXYiig0zq2daMom9dyTC3RDakSsOyzOkSr4" +
                "167zSYe8A\"," +
                "\"scope\":\"accounts payments\"," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"exp\":1723091833," +
                "\"iat\":1723088233," +
                "\"jti\":\"l0u5q6ie24x1\"," +
                "\"response_types\":[\"code id_token\"]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";;

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(
                new HashMap<>() { {
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

        RequestContextDTO requestContextDTO = new RequestContextDTO();
        requestContextDTO.setMsgInfo(msgInfoDTO);

        ExtensionResponseDTO extensionResponseDTO =  dcrHandler.postProcessRequest(requestContextDTO);

        Assert.assertNotNull(extensionResponseDTO);
        Assert.assertEquals(extensionResponseDTO.getHeaders().get("Content-Type"), "application/json");
        Assert.assertEquals(extensionResponseDTO.getPayload(),
                new ByteArrayInputStream(modifiedPayload.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testPreProcessResponse() throws OpenBankingAPIHandlerException, IOException {
        String inputPayload = "{\"client_id\":\"vFMs7_Y_ih9t0exHL9nZUw7W5kwa\",\"client_secret\":\"zFGfMcMr8HF7EbxQlpVzszVu5RGpYrEfTpAKbfu4y4Qa\",\"client_secret_expires_at\":0,\"redirect_uris\":[\"https://www.google.com/redirects/redirect1\"],\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"],\"client_name\":\"jFQuQ4eQbNCMSqdCog21nF\",\"ext_application_display_name\":null,\"ext_application_owner\":\"admin@carbon.super\",\"ext_application_token_lifetime\":3600,\"ext_user_token_lifetime\":3600,\"ext_refresh_token_lifetime\":86400,\"ext_id_token_lifetime\":3600,\"ext_pkce_mandatory\":false,\"ext_pkce_support_plain\":false,\"ext_public_client\":false,\"token_type_extension\":\"JWT\",\"ext_token_type\":\"JWT\",\"jwks_uri\":\"https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/jFQuQ4eQbNCMSqdCog21nF.jwks\",\"token_endpoint_auth_method\":\"private_key_jwt\",\"token_endpoint_auth_signing_alg\":\"PS256\",\"sector_identifier_uri\":null,\"id_token_signed_response_alg\":\"PS256\",\"id_token_encrypted_response_alg\":null,\"id_token_encrypted_response_enc\":null,\"request_object_signing_alg\":\"PS256\",\"tls_client_auth_subject_dn\":null,\"require_pushed_authorization_requests\":false,\"require_signed_request_object\":false,\"tls_client_certificate_bound_access_tokens\":true,\"subject_type\":\"public\",\"request_object_encryption_alg\":null,\"request_object_encryption_enc\":null,\"software_statement\":\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNnaFY5eE1KUk92ayJ9.eyJpc3MiOiJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLCJzb2Z0d2FyZV9tb2RlIjoiVGVzdCIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJlX2NsaWVudF9pZCI6ImpGUXVRNGVRYk5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3BlbiBCYW5raW5nIFRQUDIgKFNhbmRib3gpIiwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2ZSBUUFAgaXMgY3JlYXRlZCBmb3IgdGVzdGluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2FyZV9jbGllbnRfdXJpIjoiaHR0cHM6Ly93c28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3NvMi5qcGciLCJzb2Z0d2FyZV9yZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZGlyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRlbnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd24wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRlIjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6WyJQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0dXMiOiJBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiwib3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvMDAxNTgwMDAwMUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIUVFyWkFBWC5qd2tzIiwic29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91cmkiOiJodHRwczovL3dzbzIuY29tIiwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZV9vbl9iZWhhbGZfb2Zfb3JnIjoiV1NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiI2M3J4eWtvcDA3eWEiLCJpYXQiOjE3MjMwODkwODgsImV4cCI6MTcyMzA5MjY4OH0.UDhht9PNvNJXJX7gn7cuSX-TeTlC4DW_wwJr1vSrAN7nTzqgf6OVbspULMXR4QTZ8QfwnfjkeNmnU4OHyTMZ8PG8mCOND6OqOMsVIOP2xNBW3Gd08kQHIaz7Cjv5PCeJimfsxuBG7qqW6DNguKCgCarBwWyzWpYtWPcXSpcdR_TnjQkWhoBrsOi0LNS18yvMhxxww271bdDilE28cZkB23_cS9L8630YcGBEkzlK9MFqIJm5qOdygm1BuYX01cC-SC4wGUmMaBSn8YOWBuG7UwcIvv1QJ2OyxlsSUEdy1rn9fQfSJQRSqFIiAZfL-hyeaKRGxotTFovBXUMmv03BmA\"}";

        String expected = "{" +
                "\"token_endpoint_auth_signing_alg\":\"PS256\"," +
                "\"grant_types\":[\"authorization_code\",\"client_credentials\",\"refresh_token\"]," +
                "\"application_type\":null," +
                "\"backchannel_user_code_parameter_supported\":false," +
                "\"tls_client_auth_subject_dn\":null," +
                "\"redirect_uris\":[\"https:/www.google.com\\/redirects\\/redirect1\"]," +
                "\"client_id\":\"vFMs7_Y_ih9t0exHL9nZUw7W5kwa\"," +
                "\"token_endpoint_auth_method\":\"private_key_jwt\"," +
                "\"software_id\":\"jFQuQ4eQbNCMSqdCog21nF\"," +
                "\"software_statement\":\"eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjdlSjhTX1pndmxZeEZBRlNna" +
                "FY5eE1KUk92ayJ9.eyJpc3MiOiJPcGVuQmFua2luZyBMdGQiLCJzb2Z0d2FyZV9lbnZpcm9ubWVudCI6InNhbmRib3giLC" +
                "Jzb2Z0d2FyZV9tb2RlIjoiVGVzdCIsInNvZnR3YXJlX2lkIjoiakZRdVE0ZVFiTkNNU3FkQ29nMjFuRiIsInNvZnR3YXJl" +
                "X2NsaWVudF9pZCI6ImpGUXVRNGVRYk5DTVNxZENvZzIxbkYiLCJzb2Z0d2FyZV9jbGllbnRfbmFtZSI6IldTTzIgT3Blbi" +
                "BCYW5raW5nIFRQUDIgKFNhbmRib3gpIiwic29mdHdhcmVfY2xpZW50X2Rlc2NyaXB0aW9uIjoiVGhpcyBhbHRlcm5hdGl2" +
                "ZSBUUFAgaXMgY3JlYXRlZCBmb3IgdGVzdGluZyBwdXJwb3Nlcy4gIiwic29mdHdhcmVfdmVyc2lvbiI6MS41LCJzb2Z0d2" +
                "FyZV9jbGllbnRfdXJpIjoiaHR0cHM6Ly93c28yLmNvbSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cud3NvMi5jb20vd3Nv" +
                "Mi5qcGciLCJzb2Z0d2FyZV9yZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vd3d3Lmdvb2dsZS5jb20vcmVkaXJlY3RzL3JlZG" +
                "lyZWN0MSJdLCJzb2Z0d2FyZV9yb2xlcyI6WyJBSVNQIiwiUElTUCIsIkNCUElJIl0sIm9yZ2FuaXNhdGlvbl9jb21wZXRl" +
                "bnRfYXV0aG9yaXR5X2NsYWltcyI6eyJhdXRob3JpdHlfaWQiOiJPQkdCUiIsInJlZ2lzdHJhdGlvbl9pZCI6IlVua25vd2" +
                "4wMDE1ODAwMDAxSFFRclpBQVgiLCJzdGF0dXMiOiJBY3RpdmUiLCJhdXRob3Jpc2F0aW9ucyI6W3sibWVtYmVyX3N0YXRl" +
                "IjoiR0IiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNCUElJIl19LHsibWVtYmVyX3N0YXRlIjoiSUUiLCJyb2xlcyI6Wy" +
                "JQSVNQIiwiQ0JQSUkiLCJBSVNQIl19LHsibWVtYmVyX3N0YXRlIjoiTkwiLCJyb2xlcyI6WyJQSVNQIiwiQUlTUCIsIkNC" +
                "UElJIl19XX0sInNvZnR3YXJlX2xvZ29fdXJpIjoiaHR0cHM6Ly93c28yLmNvbS93c28yLmpwZyIsIm9yZ19zdGF0dXMiOi" +
                "JBY3RpdmUiLCJvcmdfaWQiOiIwMDE1ODAwMDAxSFFRclpBQVgiLCJvcmdfbmFtZSI6IldTTzIgKFVLKSBMSU1JVEVEIiwi" +
                "b3JnX2NvbnRhY3RzIjpbeyJuYW1lIjoiVGVjaG5pY2FsIiwiZW1haWwiOiJzYWNoaW5pc0B3c28yLmNvbSIsInBob25lIj" +
                "oiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IlRlY2huaWNhbCJ9LHsibmFtZSI6IkJ1c2luZXNzIiwiZW1haWwiOiJzYWNoaW5p" +
                "c0B3c28yLmNvbSIsInBob25lIjoiKzk0Nzc0Mjc0Mzc0IiwidHlwZSI6IkJ1c2luZXNzIn1dLCJvcmdfandrc19lbmRwb2" +
                "ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1ODAwMDAxSFFRclpBQVgvMDAxNTgw" +
                "MDAwMUhRUXJaQUFYLmp3a3MiLCJvcmdfandrc19yZXZva2VkX2VuZHBvaW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYm" +
                "Fua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkLzAwMTU4MDAwMDFIUVFyWkFBWC5qd2tzIiwi" +
                "c29mdHdhcmVfandrc19lbmRwb2ludCI6Imh0dHBzOi8va2V5c3RvcmUub3BlbmJhbmtpbmd0ZXN0Lm9yZy51ay8wMDE1OD" +
                "AwMDAxSFFRclpBQVgvakZRdVE0ZVFiTkNNU3FkQ29nMjFuRi5qd2tzIiwic29mdHdhcmVfandrc19yZXZva2VkX2VuZHBv" +
                "aW50IjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2" +
                "VkL2pGUXVRNGVRYk5DTVNxZENvZzIxbkYuandrcyIsInNvZnR3YXJlX3BvbGljeV91cmkiOiJodHRwczovL3dzbzIuY29t" +
                "Iiwic29mdHdhcmVfdG9zX3VyaSI6Imh0dHBzOi8vd3NvMi5jb20iLCJzb2Z0d2FyZV9vbl9iZWhhbGZfb2Zfb3JnIjoiV1" +
                "NPMiBPcGVuIEJhbmtpbmciLCJqdGkiOiI2M3J4eWtvcDA3eWEiLCJpYXQiOjE3MjMwODkwODgsImV4cCI6MTcyMzA5MjY4" +
                "OH0.UDhht9PNvNJXJX7gn7cuSX-TeTlC4DW_wwJr1vSrAN7nTzqgf6OVbspULMXR4QTZ8QfwnfjkeNmnU4OHyTMZ8PG8mC" +
                "OND6OqOMsVIOP2xNBW3Gd08kQHIaz7Cjv5PCeJimfsxuBG7qqW6DNguKCgCarBwWyzWpYtWPcXSpcdR_TnjQkWhoBrsOi0" +
                "LNS18yvMhxxww271bdDilE28cZkB23_cS9L8630YcGBEkzlK9MFqIJm5qOdygm1BuYX01cC-SC4wGUmMaBSn8YOWBuG7Uw" +
                "cIvv1QJ2OyxlsSUEdy1rn9fQfSJQRSqFIiAZfL-hyeaKRGxotTFovBXUMmv03BmA\"," +
                "\"client_secret_expires_at\":0," +
                "\"backchannel_client_notification_endpoint\":null," +
                "\"scope\":null," +
                "\"client_secret\":\"zFGfMcMr8HF7EbxQlpVzszVu5RGpYrEfTpAKbfu4y4Qa\"," +
                "\"client_id_issued_at\":\\d+," +
                "\"request_object_signing_alg\":\"PS256\"," +
                "\"backchannel_authentication_request_signing_alg\":null," +
                "\"backchannel_token_delivery_mode\":\"poll\"," +
                "\"response_types\":[]," +
                "\"id_token_signed_response_alg\":\"PS256\"" +
                "}";

        MsgInfoDTO msgInfoDTO = new MsgInfoDTO();
        msgInfoDTO.setResource("/register");
        msgInfoDTO.setHeaders(
                new HashMap<>() { {
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

        String actual = convert(extensionResponseDTO.getPayload());
        Assert.assertTrue(actual.matches(expected));
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
