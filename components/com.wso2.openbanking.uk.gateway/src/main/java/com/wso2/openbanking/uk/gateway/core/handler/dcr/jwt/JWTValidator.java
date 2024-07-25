package com.wso2.openbanking.uk.gateway.core.handler.dcr.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class JWTValidator {
    private static final Log log = LogFactory.getLog(JWTValidator.class);

    // Default timeout values
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    private static final int DEFAULT_READ_TIMEOUT = 3000;

    private final String jwt;
    private int connectionTimeout = 0;
    private int readTimeout = 0;

    public JWTValidator(String jwt) {
        this.jwt = jwt;

        connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        readTimeout = DEFAULT_READ_TIMEOUT;
    }

    public String getJwt() {
        return jwt;
    }

    public boolean validateSignatureUsingPublicKey(String publicKey) {
        // If the JWT is malformed or not in the correct format, return false
        if (!validateJwt()) {
            log.error("Invalid JWT format");
            throw new JWTValidatorRuntimeException("Invalid JWT format");
        }

        if (publicKey == null || publicKey.isEmpty()) {
            log.error("Invalid public key");
            throw new JWTValidatorRuntimeException("Invalid public key");
        }

        byte[] publicKeyData = null;

        try {
            publicKeyData = Base64.getDecoder().decode(publicKey);
        } catch (IllegalArgumentException e) {
            log.error("Invalid public key", e);
            throw new JWTValidatorRuntimeException("Invalid public key", e);
        }

        SignedJWT signedJWT = null;

        try {
            signedJWT = SignedJWT.parse(jwt);
        } catch (ParseException e) {
            log.error("Invalid JWT", e);
            throw new JWTValidatorRuntimeException("Invalid JWT", e);
        }

        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
        String algorithm = signedJWT.getHeader().getAlgorithm().getName();

        if (!algorithm.startsWith("RS")) {
            log.error("Invalid algorithm! Expected RSA algorithm");
            throw new JWTValidatorRuntimeException("Invalid algorithm! Expected RSA algorithm");
        }


        KeyFactory keyFactory = null;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid algorithm", e);
            throw new JWTValidatorRuntimeException("Invalid algorithm", e);
        }


        RSAPublicKey rsaPublicKey = null;
        try {
            rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid public key", e);
            throw new JWTValidatorRuntimeException("Invalid public key", e);
        }

        JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);

        try {
            return signedJWT.verify(verifier);
        } catch (JOSEException e) {
            log.error("Invalid JWT", e);
            throw new JWTValidatorRuntimeException("Invalid JWT", e);
        }
    }

    public boolean validateSignatureUsingJWKS(String jwkSetEndpoint) {
        // If the JWT is malformed or not in the correct format, return false
        if (!validateJwt()) {
            log.error("Invalid JWT format");
            throw new JWTValidatorRuntimeException("Invalid JWT format");
        }

        // Try to parse the JWT
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(jwt);
        } catch (ParseException e) {
            log.error("Invalid JWT", e);
            throw new JWTValidatorRuntimeException("Invalid JWT", e);
        }

        // Retrieve the JWK Set from the given endpoint

        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(
                connectionTimeout, readTimeout);

        URL jwkSetURL = null;

        try {
            jwkSetURL = new URL(jwkSetEndpoint);
        } catch (MalformedURLException e) {
            log.error("Invalid JWKS endpoint", e);
            throw new JWTValidatorRuntimeException("Invalid JWKS endpoint", e);
        }

        // Create the JWK Source.
        // NOTE :   In the OB3 accelerator, they have tried to cache this JWK source using a ConcurrentHashMap.
        //          I haven't done that here. If the need arises, we can implement that. Just FYI, they have recreated
        //          the hash map in every call to this method. So, they have not cached the JWK source. LOL!
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwkSetURL, resourceRetriever).build();

        // Create the key selector, so it would be able to select the correct key to verify the signature
        JWSVerificationKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                signedJWT.getHeader().getAlgorithm(),
                jwkSource
        );

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(keySelector);

        try {
            // If the processing is successful, the JWT is valid
            jwtProcessor.process(signedJWT, new SimpleSecurityContext());
            return true;
        } catch (Exception e) {
            log.debug("JWT Signature validation failed!", e);
            // If the processing fails, the JWT is invalid
            return false;
        }
    }

    public boolean validateJwt() {
        return validateJwt(jwt);
    }

    public <T> T getClaim(String key, Class<T> clazz) {
        Map<String, Object> claims = getClaims();

        if (!claims.containsKey(key)) {
            log.debug(String.format("Claim %s not found! Returning null...",
                    key.replaceAll("[\r\n]", "_")));
            return null;
        }

        Object claim = claims.get(key);

        if (clazz.isInstance(claim)) {
            return clazz.cast(claim);
        }

        log.error(String.format("Invalid claim type! Expected %s, but found %s", clazz.getName(),
                claim.getClass().getName()));
        throw new JWTValidatorRuntimeException(String.format("Invalid claim type! Expected %s, but found %s",
                clazz.getName(), claim.getClass().getName()));
    }

    public Map<String, Object> getClaims() {
        if (!validateJwt()) {
            log.error("Invalid JWT format");
            throw new JWTValidatorRuntimeException("Invalid JWT format");
        }

        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(jwt);
        } catch (ParseException e) {
            log.error("Invalid JWT", e);
            throw new JWTValidatorRuntimeException("Invalid JWT", e);
        }

        try {
            return signedJWT.getJWTClaimsSet().toJSONObject();
        } catch (ParseException e) {
            log.error("Invalid JWT", e);
            throw new JWTValidatorRuntimeException("", e);
        }
    }

    public String getJSONString() {
        Map<String, Object> claims = getClaims();
        return new JSONObject(claims).toJSONString();
    }

    private static boolean validateJwt(String jwt) {
        // Lambda expression to check if a string is Base64 URL encoded
        Function<String, Boolean> isBase64UrlEncoded = str -> {
            if (str == null || str.isEmpty()) {
                log.debug("Empty string");
                return false;
            }

            // Base64 URL encoding allows only A-Z, a-z, 0-9, '-', and '_'
            Pattern base64UrlPattern = Pattern.compile("^[A-Za-z0-9_-]+$");
            if (!base64UrlPattern.matcher(str).matches()) {
                log.debug("Invalid characters in the string");
                return false;
            }

            // Decode with padding for proper Base64 decoding
            try {
                Base64.getUrlDecoder().decode(str + "==".substring(0, (4 - str.length() % 4) % 4));
                return true;
            } catch (IllegalArgumentException e) {
                log.debug("Invalid Base64 URL encoding", e);
                return false;
            }
        };

        if (jwt == null || jwt.isEmpty()) {
            log.debug("Empty JWT");
            return false;
        }

        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            log.debug("Invalid JWT format");
            return false;
        }

        for (String part : parts) {
            if (!isBase64UrlEncoded.apply(part)) {
                log.debug("Invalid JWT format");
                return false;
            }
        }
        return true;
    }

}
