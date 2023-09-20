package com.mogak.spring.login;

import com.mogak.spring.exception.CommonException;
import com.mogak.spring.global.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthHandler {

    private final JwtTokenHandler jwtTokenHandler;

    public Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String jwtToken = extractToken(request)
                .orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        jwtToken = jwtToken.substring("Bearer ".length());
        return Long.valueOf(jwtTokenHandler.getUserPk(jwtToken));
    }


    public static Optional<String> extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (isEmptyAuthorizationHeader(token)) {
            return Optional.empty();
        }

        return Optional.of(token);
    }

    private static boolean isEmptyAuthorizationHeader(String token) {
        return !StringUtils.hasText(token);
    }
}
