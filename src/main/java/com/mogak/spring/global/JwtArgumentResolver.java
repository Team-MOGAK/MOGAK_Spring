package com.mogak.spring.global;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class JwtArgumentResolver {
    private JwtArgumentResolver() {}

    public static Optional<String> extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (isEmptyAuthorizationHeader(token)) {
            return Optional.empty();
        }

        return Optional.of(request.getParameter("userId"));
    }

    private static boolean isEmptyAuthorizationHeader(String token) {
        return !StringUtils.hasText(token);
    }
}
