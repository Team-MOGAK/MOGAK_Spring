package com.mogak.spring.auth;

import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoogleOAuthUserProviderTest {

    private final JwtDecoder jwtDecoder = mock(JwtDecoder.class);
    private final GoogleOAuthUserProvider provider = new GoogleOAuthUserProvider(jwtDecoder);

    @Test
    @DisplayName("Google id token에서 providerUserId, email, emailVerified를 추출한다")
    void extractsGoogleProfile() {
        when(jwtDecoder.decode("google-id-token")).thenReturn(jwt("google-sub", "google@test.com", true));

        SocialUserProfile profile = provider.getUser("google-id-token");

        assertThat(profile.provider()).isEqualTo(SocialProvider.GOOGLE);
        assertThat(profile.providerUserId()).isEqualTo("google-sub");
        assertThat(profile.email()).isEqualTo("google@test.com");
        assertThat(profile.emailVerified()).isTrue();
    }

    @Test
    @DisplayName("Google email_verified가 문자열이어도 검증 상태를 추출한다")
    void extractsStringEmailVerified() {
        when(jwtDecoder.decode("google-id-token")).thenReturn(jwt("google-sub", "google@test.com", "true"));

        SocialUserProfile profile = provider.getUser("google-id-token");

        assertThat(profile.emailVerified()).isTrue();
    }

    @Test
    @DisplayName("Google email_verified가 없으면 검증되지 않은 이메일로 처리한다")
    void treatsMissingEmailVerifiedAsFalse() {
        when(jwtDecoder.decode("google-id-token"))
                .thenReturn(jwt("google-sub", Map.of("email", "google@test.com")));

        SocialUserProfile profile = provider.getUser("google-id-token");

        assertThat(profile.emailVerified()).isFalse();
    }

    @Test
    @DisplayName("Google id token에 email이 없으면 SOCIAL_EMAIL_REQUIRED 예외를 반환한다")
    void rejectsMissingEmail() {
        when(jwtDecoder.decode("google-id-token"))
                .thenReturn(jwt("google-sub", Map.of("email_verified", true)));

        Throwable throwable = catchThrowable(() -> provider.getUser("google-id-token"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.SOCIAL_EMAIL_REQUIRED);
    }

    private Jwt jwt(String subject, String email, Object emailVerified) {
        return jwt(subject, Map.of(
                "email", email,
                "email_verified", emailVerified
        ));
    }

    private Jwt jwt(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .claims(values -> values.putAll(claims))
                .build();
    }
}
