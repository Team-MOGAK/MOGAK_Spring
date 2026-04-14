package com.mogak.spring.support;

import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContextTestHelper {

    private SecurityContextTestHelper() {
    }

    public static void setAuthentication(String email) {
        setAuthentication(1L, email, JwtTokenProvider.ROLE_USER);
    }

    public static void setAuthentication(Long userId, String email, String role) {
        AuthenticatedUser principal = new AuthenticatedUser(userId, email, role);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities())
        );
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
