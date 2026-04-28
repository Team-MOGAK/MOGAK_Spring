package com.mogak.spring.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {

    public String email() {
        if (kakaoAccount == null) {
            return null;
        }
        return kakaoAccount.email();
    }

    public boolean emailVerified() {
        return kakaoAccount != null &&
                Boolean.TRUE.equals(kakaoAccount.emailValid()) &&
                Boolean.TRUE.equals(kakaoAccount.emailVerified());
    }

    public record KakaoAccount(
            String email,
            @JsonProperty("is_email_valid") Boolean emailValid,
            @JsonProperty("is_email_verified") Boolean emailVerified
    ) {
    }
}
