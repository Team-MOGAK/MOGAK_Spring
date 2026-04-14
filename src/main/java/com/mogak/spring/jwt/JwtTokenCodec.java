package com.mogak.spring.jwt;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Locale;
import java.time.Duration;
import java.util.Objects;

@Component
public class JwtTokenCodec {

    private static final int MIN_HMAC_SHA_256_KEY_BYTES = 32;
    private static final String HMAC_SHA_256 = "HmacSHA256";
    static final Duration JWT_CLOCK_SKEW = Duration.ofSeconds(30);

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtTokenCodec(@Value("${jwt.secret}") String encodedSecret) {
        SecretKey secretKey = createSecretKey(encodedSecret);
        this.jwtEncoder = NimbusJwtEncoder.withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(new JwtTimestampValidator(JWT_CLOCK_SKEW));
        this.jwtDecoder = decoder;
    }

    public String encode(JwtClaimsSet claimsSet) {
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256)
                .type("jwt")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet))
                .getTokenValue();
    }

    public Jwt decode(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtValidationException exception) {
            if (isExpired(exception)) {
                throw new AuthException(ErrorCode.EXPIRE_TOKEN);
            }
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        }
    }

    private SecretKey createSecretKey(String encodedSecret) {
        byte[] decodedSecret;
        try {
            decodedSecret = Base64.getDecoder().decode(encodedSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("jwt.secret must be a Base64-encoded HMAC key", exception);
        }
        if (decodedSecret.length < MIN_HMAC_SHA_256_KEY_BYTES) {
            throw new IllegalStateException("jwt.secret must decode to at least 32 bytes for HS256");
        }
        return new SecretKeySpec(decodedSecret, HMAC_SHA_256);
    }

    private boolean isExpired(JwtValidationException exception) {
        return exception.getErrors()
                .stream()
                .map(OAuth2Error::getDescription)
                .filter(Objects::nonNull)
                .map(description -> description.toLowerCase(Locale.ROOT))
                .anyMatch(description -> description.contains("expired"));
    }
}
