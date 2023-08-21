package com.mogak.spring.exception;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class JwtArgumentResolver {
    public static Optional<Long> extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (isEmptyAuthorizationHeader(token)) {
            return Optional.empty();
        }

        return Optional.of(Long.valueOf(request.getParameter("userId")));
    }

    private static boolean isEmptyAuthorizationHeader(String token) {
        return !StringUtils.hasText(token);
    }
}
