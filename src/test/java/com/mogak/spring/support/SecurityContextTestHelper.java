package com.mogak.spring.support;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

public final class SecurityContextTestHelper {

    private SecurityContextTestHelper() {
    }

    public static void setAuthentication(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, "password", Collections.emptyList())
        );
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
