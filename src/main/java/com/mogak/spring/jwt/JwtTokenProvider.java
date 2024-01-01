package com.mogak.spring.jwt;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.redis.RedisService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expiry}")
    private Long accessTokenValidTime;
    @Value("${jwt.refresh-token-expiry}")
    private Long refreshTokenValidTime;
    private final CustomUserDetailsService userDetailsService;

    public static final String access_header = "Authorization";
    public static final String refresh_header = "RefreshToken";

    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("id", userId)
                .claim("email", email)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * access token 검증
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            parseToken(accessToken);
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.EXPIRE_TOKEN);
        } catch (SignatureException | UnsupportedJwtException e) {
            throw new AuthException(ErrorCode.WRONG_TOKEN);
        }
        return true;
    }

    /**
     * claims 추출
     */
    public Jws<Claims> parseToken(String token) {
        Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return jws;
    }

    public boolean isExpired(String token, Date date) {
        try {
            Jws<Claims> claims = parseToken(token);
            return !claims.getBody().getExpiration().before(date); //만료시간이 현재시간 이전이 아니라면 true, 만료되었다면 false
        } catch (Exception e) {
            throw new BaseException(ErrorCode.EXPIRE_TOKEN);
        }
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
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("email").toString();
    }

    /**
     * 토큰 갱신
     */
    public JwtTokens refresh(String refreshToken, Long userId, String email) {
        Date date = new Date();
        if (!isExpired(refreshToken, date)) { //만료되었으면
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
    public String getEmailByRefresh(String refreshToken) throws JwtException {
        try {
            Jws<Claims> claims = parseToken(refreshToken);
            String email = claims.getBody().getSubject();
            return email;
        } catch (JwtException e) {
            throw new JwtException("Invalid Refresh Token");
        }
    }

    /**
     * refresh 토큰 유효 여부
     */
    public boolean isRefreshable(String refreshToken) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 3);//현재시간으로부터 3일 후까지 리프레시 가능하도록
        return !isExpired(refreshToken, calendar.getTime());
    }
}
