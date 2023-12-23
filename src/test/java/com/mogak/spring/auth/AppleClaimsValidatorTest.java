package com.mogak.spring.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleClaimsValidatorTest {

    private static final String ISS = "iss";
    private static final String CLIENT_ID = "aud";
    private static final String NONCE = "nonce";
    private static final String NONCE_KEY = "nonce";

    private final AppleClaimsValidator appleClaimsValidator = new AppleClaimsValidator(ISS, CLIENT_ID);

    @Test
    @DisplayName("올바른 Claims 이면 true 반환한다")
    void isValid() {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(NONCE_KEY, EncryptUtils.encrypt(NONCE));

        Claims claims = Jwts.claims(claimsMap)
                .setIssuer(ISS)
                .setAudience(CLIENT_ID);

        assertThat(appleClaimsValidator.isValid(claims)).isTrue();
    }
}
