package com.mogak.spring.web.dto;

import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class CreateUserDto{
        private String nickname;
        private String email;
    }

    @Getter
    public static class UpdateUserDto{
        public String contents;
    }
}
