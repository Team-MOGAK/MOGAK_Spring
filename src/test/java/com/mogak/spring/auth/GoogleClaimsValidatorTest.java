package com.mogak.spring.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleClaimsValidatorTest {

    private static final String ISSUER = "https://accounts.google.com";
    private static final String ANDROID_CLIENT_ID = "android-client-id";
    private static final String WEB_CLIENT_ID = "web-client-id";

    private final GoogleClaimsValidator validator =
            new GoogleClaimsValidator(ISSUER, ANDROID_CLIENT_ID + "," + WEB_CLIENT_ID);

    @Test
    @DisplayName("복수 client id 중 하나가 audience에 포함되면 성공한다")
    void validatesOneOfAllowedAudiences() {
        Jwt claims = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("iss", ISSUER)
                .claim("aud", java.util.List.of(WEB_CLIENT_ID))
                .build();

        assertThat(validator.validate(claims).hasErrors()).isFalse();
    }

    @Test
    @DisplayName("audience가 허용된 client id를 포함하지 않으면 실패한다")
    void rejectsUnknownAudience() {
        Jwt claims = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("iss", ISSUER)
                .claim("aud", java.util.List.of("other-client-id"))
                .build();

        assertThat(validator.validate(claims).hasErrors()).isTrue();
    }
}
