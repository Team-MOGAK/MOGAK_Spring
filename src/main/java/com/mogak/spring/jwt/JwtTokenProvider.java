package com.mogak.spring.jwt;

import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expiry}")
    private Long accessTokenValidTime;
    @Value("${jwt.refresh-token-expiry}")
    private Long refreshTokenValidTime;
    private CustomUserDetailsService userDetailsService;

    public static final String access_header = "Authorization";
    public static final String refresh_header = "RefreshToken";

    public String createAccessToken(Long userId, String email){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("id", userId)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(){
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public Jws<Claims> parseToken(String token){
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

    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserPk(token));
        if(userDetails == null){
            throw new UsernameNotFoundException("User not found for useremail: " + getUserPk(token));
        }
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    //현재 인증된 user 정보 조회
    public CustomUserDetails getSecurityContextHolder(){
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader(access_header).replace("Bearer","").trim();
    }
    //user email 검색
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("email").toString();
    }

    /**
     * 토큰 갱신
     */
    public JwtTokens refresh(String refreshToken, Long userId, String email){
        Date date = new Date();
        if(!isExpired(refreshToken, date)){ //만료되었으면
            throw new IllegalStateException("EXPIRED_REFRESH_TOKEN");
        }
        String accessToken = createAccessToken(userId, email);

        String localRefreshToken = refreshToken;
        if(isRefreshable(refreshToken)){
            localRefreshToken = createRefreshToken();
        }
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(localRefreshToken)
                .build();
    }

    /**
     * refresh 토큰 유효 여부
     */
    public boolean isRefreshable(String refreshToken){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 3);//현재시간으로부터 3일 후까지 리프레시 가능하도록
        return !isExpired(refreshToken, calendar.getTime());
    }
}