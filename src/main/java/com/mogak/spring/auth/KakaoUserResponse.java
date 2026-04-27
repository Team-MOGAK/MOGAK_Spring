package com.mogak.spring.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserResponse {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public String email() {
        if (kakaoAccount == null) {
            return null;
        }
        return kakaoAccount.email;
    }

    public boolean emailVerified() {
        return kakaoAccount != null &&
                Boolean.TRUE.equals(kakaoAccount.emailValid) &&
                Boolean.TRUE.equals(kakaoAccount.emailVerified);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class KakaoAccount {
        private String email;
        @JsonProperty("is_email_valid")
        private Boolean emailValid;
        @JsonProperty("is_email_verified")
        private Boolean emailVerified;
    }
}
