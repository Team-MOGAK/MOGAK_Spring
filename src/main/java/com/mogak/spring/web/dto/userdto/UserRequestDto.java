package com.mogak.spring.web.dto.userdto;

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
    }
    @Builder
    @Getter
    public static class UploadImageDto {
        private String imgName;
        private String imgUrl;
    }

    @Getter
    public static class UpdateUserDto{
        public String contents;
    }

    @Getter
    public static class UpdateNicknameDto {
        private String nickname;
    }

    @Getter
    public static class UpdateJobDto {
        private String job;
    }

    @Builder
    @Getter
    public static class UpdateImageDto {
        private String imgName;
        private String imgUrl;
    }
}
