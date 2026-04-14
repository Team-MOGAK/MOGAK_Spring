package com.mogak.spring.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.security.SecurityAuthority;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.access-token-expiry}")
    private Long accessTokenValidTime;
    @Value("${jwt.refresh-token-expiry}")
    private Long refreshTokenValidTime;
    private final JwtTokenCodec jwtTokenCodec;

    public static final String access_header = "Authorization";
    public static final String refresh_header = "RefreshToken";
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    public String createAccessToken(Long userId, String email) {
        return createAccessToken(userId, email, SecurityAuthority.USER.getAuthority());
    }

    public String createAccessToken(Long userId, String email, String role) {
        java.time.Instant now = java.time.Instant.now();
        return jwtTokenCodec.encode(org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                .claim("id", userId)
                .claim("email", email)
                .claim("role", role)
                .claim("token_type", ACCESS_TOKEN_TYPE)
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessTokenValidTime))
                .build());
    }

    public String createRefreshToken(String email) {
        java.time.Instant now = java.time.Instant.now();
        return jwtTokenCodec.encode(org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                .claim("token_type", REFRESH_TOKEN_TYPE)
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusMillis(refreshTokenValidTime))
                .build());
    }

    /**
     * access token 검증
     */
    public boolean validateAccessToken(String accessToken) {
        parseToken(accessToken);
        return true;
    }

    /**
     * claims 추출
     */
    public Jwt parseToken(String token) {
        return jwtTokenCodec.decode(token);
    }

    public boolean isNotExpiredAt(String token, Date date) {
        Jwt claims = parseToken(token);
        return claims.getExpiresAt() != null && !claims.getExpiresAt().plus(JwtTokenCodec.JWT_CLOCK_SKEW).isBefore(date.toInstant());
    }

    /**
     * 토큰 갱신
     */
    public JwtTokens refresh(String refreshToken, Long userId, String email) {
        return refresh(refreshToken, userId, email, SecurityAuthority.USER.getAuthority());
    }

    public JwtTokens refresh(String refreshToken, Long userId, String email, String role) {
        Date date = new Date();
        if (!isNotExpiredAt(refreshToken, date)) {
            throw new AuthException(ErrorCode.EXPIRE_TOKEN);
        }
        parseRefreshToken(refreshToken);
        String accessToken = createAccessToken(userId, email, role);

        String localRefreshToken = refreshToken;
        if (isRefreshable(refreshToken)) { //만료되었으면 재발급
            localRefreshToken = createRefreshToken(email);
        }
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(localRefreshToken)
                .build();
    }

    /**
     * refresh 토큰 이메일 추출
     */
    public String getEmailByRefresh(String refreshToken) {
        return parseRefreshToken(refreshToken).getSubject();
    }

    private Jwt parseRefreshToken(String refreshToken) {
        Jwt claims = parseToken(refreshToken);
        if (!REFRESH_TOKEN_TYPE.equals(claims.getClaimAsString("token_type"))) {
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        }
        return claims;
    }

    /**
     * refresh 토큰 유효 여부
     */
    public boolean isRefreshable(String refreshToken) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 3);//현재시간으로부터 3일 후까지 리프레시 가능하도록
        return !isNotExpiredAt(refreshToken, calendar.getTime());
    }

}
