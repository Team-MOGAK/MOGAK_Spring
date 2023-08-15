package com.mogak.spring.web.dto;

import lombok.*;

public class UserResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class toCreateDto {
        private Long userId;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LoginDto {
        private String token;
    }
}
