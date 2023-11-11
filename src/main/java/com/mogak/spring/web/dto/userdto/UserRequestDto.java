package com.mogak.spring.web.dto.userdto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class UserRequestDto {

    @Builder
    @Getter
    public static class CreateUserDto {
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Max(value = 10, message = "닉네임은 최대 10자입니다.")
        private String nickname;
        @Max(value = 100)
        private String job;
        @Max(100)
        private String address;
        @Max(100)
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
