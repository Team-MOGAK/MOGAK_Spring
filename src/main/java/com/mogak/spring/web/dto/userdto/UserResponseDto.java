package com.mogak.spring.web.dto.userdto;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.jwt.JwtTokens;
import lombok.*;

public class UserResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateDto {
        private Long userId;
        private String nickname;
        private JwtTokens tokens;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LoginDto {
        private String token;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserDto {
        private String nickname;
        private String job;

        public static UserDto from(User user) {
            return UserDto.builder()
                    .nickname(user.getNickname())
                    .job(user.getJob().getName())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetUserDto {
        private String nickname;
        private String job;
        private String imgUrl;
    }
}
