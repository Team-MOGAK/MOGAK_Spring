package com.mogak.spring.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

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
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenCodec jwtTokenCodec;

    public static final String access_header = "Authorization";
    public static final String refresh_header = "RefreshToken";

    public String createAccessToken(Long userId, String email) {
        java.time.Instant now = java.time.Instant.now();
        return jwtTokenCodec.encode(org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                .claim("id", userId)
                .claim("email", email)
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessTokenValidTime))
                .build());
    }

    public String createRefreshToken(String email) {
        java.time.Instant now = java.time.Instant.now();
        return jwtTokenCodec.encode(org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
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

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserPk(token));
        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found for useremail: " + getUserPk(token));
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //현재 인증된 user 정보 조회
    public CustomUserDetails getSecurityContextHolder() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //header에서 access token 가져오기
    public String resolveAccessToken(HttpServletRequest request) {
        if (request.getHeader(access_header) != null && request.getHeader(access_header).startsWith("Bearer ")) {
            return request.getHeader(access_header).substring(7);
        }
        return null;
    }

    //user email 검색
    public String getUserPk(String token) {
        return parseToken(token).getClaimAsString("email");
    }

    /**
     * 토큰 갱신
     */
    public JwtTokens refresh(String refreshToken, Long userId, String email) {
        Date date = new Date();
        if (!isNotExpiredAt(refreshToken, date)) {
            throw new IllegalStateException("EXPIRED_REFRESH_TOKEN");
        }
        String accessToken = createAccessToken(userId, email);

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
        return parseToken(refreshToken).getSubject();
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
