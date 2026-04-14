package com.mogak.spring.login;

import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenHandler {

    private final JwtTokenCodec jwtTokenCodec;

    public static final String AUTHORIZATION = "Authorization";

    private final long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 365; // 24시간 * 30 => 한달

    public String createJwtToken(String userPk) {
        Instant now = Instant.now();
        return jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", userPk)
                .issuedAt(now)
                .expiresAt(now.plusMillis(TOKEN_VALID_TIME))
                .build());
    }

    public String getUserPk(String token) {
        return jwtTokenCodec.decode(token).getClaimAsString("id");
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
        jwtTokenCodec.decode(parseToken(token));
    }

    private String parseToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length());
        }
        throw new BaseException(ErrorCode.WRONG_TOKEN);
    }

    public Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String jwtToken = extractToken(request).orElseThrow(() -> new BaseException(ErrorCode.EMPTY_TOKEN));
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
