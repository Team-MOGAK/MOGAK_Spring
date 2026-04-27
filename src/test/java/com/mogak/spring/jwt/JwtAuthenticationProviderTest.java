package com.mogak.spring.jwt;

import com.mogak.spring.security.SecurityAuthority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAuthenticationProviderTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("mogak-test-jwt-secret-for-hs256!".getBytes(StandardCharsets.UTF_8));

    private final JwtTokenCodec jwtTokenCodec = new JwtTokenCodec(SECRET);
    private final JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtTokenCodec);

    @Test
    @DisplayName("필수 access claim이 있으면 AuthenticatedUser 인증으로 변환한다")
    void authenticatesAccessTokenWithRequiredClaims() {
        String token = encodeToken(JwtTokenProvider.ACCESS_TOKEN_TYPE, SecurityAuthority.PENDING.getAuthority());

        Authentication authentication = jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken(token));

        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isInstanceOf(AuthenticatedUser.class);
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        assertThat(principal.getUserId()).isEqualTo(10L);
        assertThat(principal.getUsername()).isEqualTo("pending@test.com");
        assertThat(principal.getRole()).isEqualTo(SecurityAuthority.PENDING.getAuthority());
        assertThat(authentication.getAuthorities()).extracting("authority").containsExactly(SecurityAuthority.PENDING.getAuthority());
    }

    @Test
    @DisplayName("email claim이 없어도 userId subject access token은 인증한다")
    void authenticatesAccessTokenWithoutEmail() {
        Instant now = Instant.now();
        String token = jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", 10L)
                .claim("role", SecurityAuthority.PENDING.getAuthority())
                .claim("token_type", JwtTokenProvider.ACCESS_TOKEN_TYPE)
                .subject("10")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build());

        Authentication authentication = jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken(token));

        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        assertThat(principal.getUserId()).isEqualTo(10L);
        assertThat(principal.getEmail()).isNull();
    }

    @Test
    @DisplayName("token_type이 access가 아니면 인증을 거부한다")
    void rejectsNonAccessTokenType() {
        String token = encodeToken(JwtTokenProvider.REFRESH_TOKEN_TYPE, SecurityAuthority.USER.getAuthority());

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken(token)))
                .isInstanceOf(JwtAuthenticationException.class)
                .extracting("errorCode")
                .isEqualTo(com.mogak.spring.global.ErrorCode.WRONG_TOKEN);
    }

    @Test
    @DisplayName("role claim이 없는 레거시 access token은 인증을 거부한다")
    void rejectsLegacyAccessTokenWithoutRoleClaim() {
        Instant now = Instant.now();
        String token = jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", 10L)
                .claim("email", "pending@test.com")
                .claim("token_type", JwtTokenProvider.ACCESS_TOKEN_TYPE)
                .subject("10")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build());

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken(token)))
                .isInstanceOf(JwtAuthenticationException.class)
                .extracting("errorCode")
                .isEqualTo(com.mogak.spring.global.ErrorCode.WRONG_TOKEN);
    }

    @Test
    @DisplayName("만료 access token은 EXPIRE_TOKEN으로 거부한다")
    void rejectsExpiredAccessTokenWithExpireTokenErrorCode() {
        Instant now = Instant.now();
        String token = jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", 10L)
                .claim("email", "pending@test.com")
                .claim("role", SecurityAuthority.PENDING.getAuthority())
                .claim("token_type", JwtTokenProvider.ACCESS_TOKEN_TYPE)
                .subject("10")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(now.minusSeconds(60))
                .build());

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken(token)))
                .isInstanceOf(JwtAuthenticationException.class)
                .extracting("errorCode")
                .isEqualTo(com.mogak.spring.global.ErrorCode.EXPIRE_TOKEN);
    }

    @Test
    @DisplayName("형식이 잘못된 access token은 WRONG_TOKEN으로 거부한다")
    void rejectsMalformedAccessTokenWithWrongTokenErrorCode() {
        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(new JwtAuthenticationToken("not-a-jwt")))
                .isInstanceOf(JwtAuthenticationException.class)
                .extracting("errorCode")
                .isEqualTo(com.mogak.spring.global.ErrorCode.WRONG_TOKEN);
    }

    private String encodeToken(String tokenType, String role) {
        Instant now = Instant.now();
        return jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", 10L)
                .claim("email", "pending@test.com")
                .claim("role", role)
                .claim("token_type", tokenType)
                .subject("10")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build());
    }
}
