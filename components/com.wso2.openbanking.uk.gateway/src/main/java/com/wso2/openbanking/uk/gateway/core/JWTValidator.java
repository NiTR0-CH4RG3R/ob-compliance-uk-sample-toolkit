package com.wso2.openbanking.uk.gateway.core;

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
import com.wso2.openbanking.uk.common.util.SkipTestCoverage;
import com.wso2.openbanking.uk.gateway.exception.JWTValidatorRuntimeException;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * JWT Validator class to validate the format, and signature of a JWT using JWKS. This class can also be used to
 * retrieve claims from the JWT.
 */
public class JWTValidator {
    private static final Log log = LogFactory.getLog(JWTValidator.class);

    // Default timeout values
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    private static final int DEFAULT_READ_TIMEOUT = 3000;

    private final String jwt;
    private int connectionTimeout = 0;
    private int readTimeout = 0;

    /**
     * Constructor to create a JWTValidator object with the given JWT, connection timeout, and read timeout.
     *
     * @param jwt               The JWT to validate.
     * @param connectionTimeout The connection timeout in milliseconds.
     * @param readTimeout       The read timeout in milliseconds.
     */
    public JWTValidator(String jwt, int connectionTimeout, int readTimeout) {
        this.jwt = jwt;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Constructor to create a JWTValidator object with the given JWT.
     *
     * @param jwt The JWT to validate.
     */
    public JWTValidator(String jwt) {
        this(jwt, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Get the JWT.
     *
     * @return The JWT.
     */
    public String getJwt() {
        return jwt;
    }

    /**
     * Validate the signature of the JWT using public key retrieved from a REST endpoint. If the endpoint need Mutual
     * TLS, then the endpoint's public certificate must be in the truststore.
     *
     * @param jwkSetEndpoint JWK Set endpoint.
     * @return True if the signature can be validated using the public keys retrieved from the endpoint.
     * @throws JWTValidatorRuntimeException If any error occurs.
     */
    @SkipTestCoverage(message = "Testing on this function is skipped due to it involving sending a http request")
    public boolean validateSignatureUsingJWKS(String jwkSetEndpoint) throws JWTValidatorRuntimeException {
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

    /**
     * Validates the format of the JWT.
     *
     * @return True if the given string is a valid JWT.
     */
    public boolean validateJwt() {
        return validateJwt(jwt);
    }

    /**
     * Extract claims from the JWT body, and cast it into a given class type.
     *
     * @param key   Key of the claim.
     * @param clazz Class type you want to cast the value of the claim.
     * @param <T>   Class Type.
     * @return The value of the claim. Returns null if the key doesn't exist.
     * @throws JWTValidatorRuntimeException If the given claim cannot be cast to the given type.
     */
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

    /**
     * Get the JWT body claims as a java Map.
     *
     * @return Claim map.
     */
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

    /**
     * Get the JWT body as a JSON string.
     *
     * @return JSON String.
     */
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
