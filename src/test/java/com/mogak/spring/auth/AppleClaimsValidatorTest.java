package com.mogak.spring.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleClaimsValidatorTest {

    private static final String ISS = "https://appleid.apple.com";
    private static final String CLIENT_ID = "aud";

    private final AppleClaimsValidator appleClaimsValidator = new AppleClaimsValidator(ISS, CLIENT_ID);

    @Test
    @DisplayName("올바른 Claims 이면 true 반환한다")
    void isValid() {
        Jwt claims = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("iss", ISS)
                .claim("aud", java.util.List.of(CLIENT_ID))
                .claims(values -> values.putAll(Map.of("nonce", "nonce")))
                .build();

        assertThat(appleClaimsValidator.isValid(claims)).isTrue();
    }

    @Test
    @DisplayName("URL로 변환할 수 없는 issuer는 false를 반환한다")
    void returnsFalseWhenIssuerCannotBeConvertedToUrl() {
        Jwt claims = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("iss", "not-apple")
                .claim("aud", java.util.List.of(CLIENT_ID))
                .build();

        assertThat(appleClaimsValidator.isValid(claims)).isFalse();
    }

    @Test
    @DisplayName("audience가 client id를 포함하지 않으면 false를 반환한다")
    void returnsFalseWhenAudienceDoesNotContainClientId() {
        Jwt claims = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("iss", ISS)
                .claim("aud", java.util.List.of("other-client"))
                .build();

        assertThat(appleClaimsValidator.isValid(claims)).isFalse();
    }
}
