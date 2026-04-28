package com.mogak.spring.auth;

public record AppleUserResponse(
        String providerUserId,
        String email,
        Boolean emailVerified
) {
    public AppleUserResponse(String email) {
        this(null, email, false);
    }

    public AppleUserResponse(String providerUserId, String email) {
        this(providerUserId, email, false);
    }
}
