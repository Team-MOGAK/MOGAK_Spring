package com.mogak.spring.web.dto;

import lombok.Builder;
import lombok.Getter;

public class UserRequestDto {

    @Builder
    @Getter
    public static class CreateUserDto {
        private String nickname;
        private String job;
        private String address;
        private String email;
        private String profileImg;
    }

    @Getter
    public static class UpdateUserDto{
        public String contents;
    }

    @Getter
    public static class UpdateNicknameDto {
        private String nickname;
    }
}
