package com.mogak.spring.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppleUserResponse {
    private String providerUserId;
    private String email;
    private Boolean emailVerified;

    public AppleUserResponse(String email) {
        this(null, email, false);
    }

    public AppleUserResponse(String providerUserId, String email) {
        this(providerUserId, email, false);
    }

    public AppleUserResponse(String providerUserId, String email, Boolean emailVerified) {
        this.providerUserId = providerUserId;
        this.email = email;
        this.emailVerified = emailVerified;
    }
}
