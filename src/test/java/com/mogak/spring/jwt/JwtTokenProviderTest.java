package com.mogak.spring.jwt;

import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

class JwtTokenProviderTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("mogak-test-jwt-secret-for-hs256!".getBytes(StandardCharsets.UTF_8));
    private static final String OTHER_SECRET = Base64.getEncoder()
            .encodeToString("mogak-other-jwt-secret-for-hs256".getBytes(StandardCharsets.UTF_8));

    @Test
    @DisplayName("access token은 id와 email, role, token_type claim을 담아 발급한다")
    void createAccessTokenContainsIdAndEmailClaims() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 900_000L, 2_678_400_000L);

        String token = jwtTokenProvider.createAccessToken(1L, "user@test.com");
        Jwt parsed = jwtTokenProvider.parseToken(token);

        assertThat(((Number) parsed.getClaim("id")).longValue()).isEqualTo(1L);
        assertThat(parsed.getClaimAsString("email")).isEqualTo("user@test.com");
        assertThat(parsed.getClaimAsString("role")).isEqualTo(SecurityAuthority.USER.getAuthority());
        assertThat(parsed.getClaimAsString("token_type")).isEqualTo(JwtTokenProvider.ACCESS_TOKEN_TYPE);
        assertThat(parsed.getSubject()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("refresh token의 subject에서 email을 추출한다")
    void getEmailByRefresh() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 7_200_000L, 2_678_400_000L);
        String refreshToken = jwtTokenProvider.createRefreshToken("user@test.com");

        assertThat(jwtTokenProvider.getEmailByRefresh(refreshToken)).isEqualTo("user@test.com");
        assertThat(jwtTokenProvider.parseToken(refreshToken).getClaimAsString("token_type")).isEqualTo(JwtTokenProvider.REFRESH_TOKEN_TYPE);
    }

    @Test
    @DisplayName("token_type이 없는 레거시 refresh token은 거부한다")
    void getEmailByRefreshRejectsLegacyRefreshTokenWithoutTokenType() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 900_000L, 2_678_400_000L);
        Instant now = Instant.now();
        String legacyRefreshToken = new JwtTokenCodec(SECRET).encode(JwtClaimsSet.builder()
                .subject("user@test.com")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build());

        Throwable throwable = catchThrowable(() -> jwtTokenProvider.getEmailByRefresh(legacyRefreshToken));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.WRONG_TOKEN);
    }

    @Test
    @DisplayName("refresh token 만료가 3일 이내이면 refresh token을 회전한다")
    void refreshIssuesRefreshableTokenWhenWithinRefreshableWindow() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 7_200_000L, 1_000L);
        String refreshToken = jwtTokenProvider.createRefreshToken("user@test.com");

        JwtTokens jwtTokens = jwtTokenProvider.refresh(refreshToken, 1L, "user@test.com");

        assertThat(jwtTokens.getAccessToken()).isNotBlank();
        assertThat(jwtTokens.getRefreshToken()).isNotBlank();
        assertThat(jwtTokenProvider.getEmailByRefresh(jwtTokens.getRefreshToken())).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("만료된 access token은 EXPIRE_TOKEN으로 거부한다")
    void validateExpiredAccessToken() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 7_200_000L, 2_678_400_000L);
        Instant now = Instant.now();
        String token = new JwtTokenCodec(SECRET).encode(JwtClaimsSet.builder()
                .claim("id", 1L)
                .claim("email", "user@test.com")
                .subject("user@test.com")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(now.minusSeconds(60))
                .build());

        Throwable throwable = catchThrowable(() -> jwtTokenProvider.validateAccessToken(token));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.EXPIRE_TOKEN);
    }

    @Test
    @DisplayName("30초 이내 clock skew가 있는 access token은 허용한다")
    void validateAccessTokenWithinClockSkew() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 7_200_000L, 2_678_400_000L);
        Instant now = Instant.now();
        String token = new JwtTokenCodec(SECRET).encode(JwtClaimsSet.builder()
                .claim("id", 1L)
                .claim("email", "user@test.com")
                .subject("user@test.com")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(now.minusSeconds(10))
                .build());

        assertThat(jwtTokenProvider.validateAccessToken(token)).isTrue();
    }

    @Test
    @DisplayName("수동 만료 비교도 30초 이내 clock skew를 허용한다")
    void isNotExpiredAtAllowsClockSkew() {
        JwtTokenProvider jwtTokenProvider = createProvider(SECRET, 7_200_000L, 2_678_400_000L);
        Instant now = Instant.now();
        String token = new JwtTokenCodec(SECRET).encode(JwtClaimsSet.builder()
                .claim("id", 1L)
                .claim("email", "user@test.com")
                .subject("user@test.com")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(now.minusSeconds(10))
                .build());

        assertThat(jwtTokenProvider.isNotExpiredAt(token, Date.from(now))).isTrue();
    }

    @Test
    @DisplayName("서명이 다른 access token은 WRONG_TOKEN으로 거부한다")
    void validateAccessTokenWithInvalidSignature() {
        JwtTokenProvider issuer = createProvider(SECRET, 7_200_000L, 2_678_400_000L);
        JwtTokenProvider verifier = createProvider(OTHER_SECRET, 7_200_000L, 2_678_400_000L);
        String token = issuer.createAccessToken(1L, "user@test.com");

        Throwable throwable = catchThrowable(() -> verifier.validateAccessToken(token));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.WRONG_TOKEN);
    }

    @Test
    @DisplayName("jwt.secret은 Base64 형식이어야 한다")
    void rejectsNonBase64Secret() {
        assertThatThrownBy(() -> new JwtTokenCodec("not-base64-secret"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Base64");
    }

    @Test
    @DisplayName("jwt.secret은 HS256을 위해 32바이트 이상이어야 한다")
    void rejectsShortSecret() {
        String shortSecret = Base64.getEncoder()
                .encodeToString("short-secret".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> new JwtTokenCodec(shortSecret))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }

    private JwtTokenProvider createProvider(String secret, long accessTokenValidTime, long refreshTokenValidTime) {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(new JwtTokenCodec(secret));
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidTime", accessTokenValidTime);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidTime", refreshTokenValidTime);
        return jwtTokenProvider;
    }
}
