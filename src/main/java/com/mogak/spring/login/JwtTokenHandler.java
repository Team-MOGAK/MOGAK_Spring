package com.mogak.spring.login;

import com.mogak.spring.exception.CommonException;
import com.mogak.spring.global.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenHandler {

    @Value("${jwt.secret}")
    private String secretKey;

    public static final String AUTHORIZATION = "Authorization";

    private final long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 365; // 24시간 * 30 => 한달

    public String createJwtToken(String userPk) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userPk", userPk)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userPk", String.class);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("access-token");
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String token = null;
        Cookie cookie = WebUtils.getCookie(request, "refresh-token");
        if (cookie != null)
            token = cookie.getValue();
        return token;
    }

    public void validateToken(String token) {
        try {
            token = parseToken(token);
            if (!validateExpireToken(token)) {
                throw new CommonException(ErrorCode.WRONG_TOKEN);
            }
        } catch (RuntimeException e) {
            throw new CommonException(ErrorCode.WRONG_TOKEN);
        }
    }

    private String parseToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length());
        }
        throw new CommonException(ErrorCode.WRONG_TOKEN);
    }

    /**
     * 토큰 만료 검증
     * */
    private boolean validateExpireToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new CommonException(ErrorCode.WRONG_TOKEN);
        }
    }

    public Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String jwtToken = extractToken(request)
                .orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        jwtToken = jwtToken.substring("Bearer ".length());
        return Long.valueOf(getUserPk(jwtToken));
    }


    public static Optional<String> extractToken(HttpServletRequest request) {
        String token = request.getHeader(JwtTokenHandler.AUTHORIZATION);
        if (isEmptyAuthorizationHeader(token)) {
            return Optional.empty();
        }

        return Optional.of(token);
    }

    private static boolean isEmptyAuthorizationHeader(String token) {
        return !StringUtils.hasText(token);
    }

}