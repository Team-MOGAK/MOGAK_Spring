package com.mogak.spring.login;

import com.mogak.spring.exception.CommonException;
import com.mogak.spring.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

@Component
public class JwtTokenProvider {

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

//    public Authentication getAuthentication(String token) {
//        String email = getUserPk(token);
//        User user = userService.getUser(email);
//        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
//    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userPk", String.class);
    }

    public String extract(HttpServletRequest request, String type) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(type.toLowerCase())) {
                return value.substring(type.length()).trim();
            }
        }
        return Strings.EMPTY;
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

    /**
     * 토큰 검증
     * */
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new CommonException(ErrorCode.WRONG_TOKEN);
        }
    }

}