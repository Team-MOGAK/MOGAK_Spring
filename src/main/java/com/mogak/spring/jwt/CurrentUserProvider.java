package com.mogak.spring.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class CurrentUserProvider {

    public Optional<AuthenticatedUser> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser authenticatedUser) {
            return Optional.of(authenticatedUser);
        }
        return Optional.empty();
    }

    public AuthenticatedUser requireCurrentUser() {
        return currentUser().orElseThrow(() -> new IllegalStateException("Authenticated user is not available"));
    }

    public Long currentUserId() {
        return requireCurrentUser().getUserId();
    }

    public String currentEmail() {
        return requireCurrentUser().getUsername();
    }

    public String currentRole() {
        return requireCurrentUser().getRole();
    }
}
