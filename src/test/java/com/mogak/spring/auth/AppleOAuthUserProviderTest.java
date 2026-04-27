package com.mogak.spring.auth;

import com.mogak.spring.domain.user.SocialProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.PublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppleOAuthUserProviderTest {

    private final AppleJwtParser appleJwtParser = mock(AppleJwtParser.class);
    private final AppleClient appleClient = mock(AppleClient.class);
    private final PublicKeyGenerator publicKeyGenerator = mock(PublicKeyGenerator.class);
    private final AppleClaimsValidator appleClaimsValidator = mock(AppleClaimsValidator.class);
    private final AppleOAuthUserProvider provider =
            new AppleOAuthUserProvider(appleJwtParser, appleClient, publicKeyGenerator, appleClaimsValidator);

    @Test
    @DisplayName("Apple id token에서 providerUserId, email, emailVerified를 추출한다")
    void extractsAppleProfile() {
        stubAppleClaims(jwt("apple-sub", "apple@test.com", true));

        SocialUserProfile profile = provider.getUser("apple-id-token");

        assertThat(profile.provider()).isEqualTo(SocialProvider.APPLE);
        assertThat(profile.providerUserId()).isEqualTo("apple-sub");
        assertThat(profile.email()).isEqualTo("apple@test.com");
        assertThat(profile.emailVerified()).isTrue();
    }

    @Test
    @DisplayName("Apple email_verified가 문자열이어도 검증 상태를 추출한다")
    void extractsStringEmailVerified() {
        stubAppleClaims(jwt("apple-sub", "apple@test.com", "true"));

        SocialUserProfile profile = provider.getUser("apple-id-token");

        assertThat(profile.emailVerified()).isTrue();
    }

    @Test
    @DisplayName("Apple email_verified가 없으면 검증되지 않은 이메일로 처리한다")
    void treatsMissingEmailVerifiedAsFalse() {
        stubAppleClaims(jwt("apple-sub", Map.of("email", "apple@test.com")));

        SocialUserProfile profile = provider.getUser("apple-id-token");

        assertThat(profile.emailVerified()).isFalse();
    }

    @Test
    @DisplayName("Apple 기존 연결 계정 처리를 위해 email이 없어도 providerUserId를 반환한다")
    void extractsProviderUserIdWithoutEmail() {
        stubAppleClaims(jwt("apple-sub", Map.of("email_verified", false)));

        SocialUserProfile profile = provider.getUser("apple-id-token");

        assertThat(profile.providerUserId()).isEqualTo("apple-sub");
        assertThat(profile.email()).isNull();
        assertThat(profile.emailVerified()).isFalse();
    }

    private void stubAppleClaims(Jwt claims) {
        Map<String, String> headers = Map.of("alg", "RS256", "kid", "kid");
        ApplePublicKeys keys = new ApplePublicKeys();
        PublicKey publicKey = mock(PublicKey.class);

        when(appleJwtParser.parseHeaders("apple-id-token")).thenReturn(headers);
        when(appleClient.getApplePublicKeys()).thenReturn(keys);
        when(publicKeyGenerator.generatePublicKey(headers, keys)).thenReturn(publicKey);
        when(appleJwtParser.parsePublicKeyAndGetClaims("apple-id-token", publicKey)).thenReturn(claims);
        when(appleClaimsValidator.isValid(claims)).thenReturn(true);
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
                .issuer("https://appleid.apple.com")
                .audience(List.of("client-id"))
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .claims(values -> values.putAll(claims))
                .build();
    }
}
