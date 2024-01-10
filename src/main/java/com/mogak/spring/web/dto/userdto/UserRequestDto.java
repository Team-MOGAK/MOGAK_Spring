package com.mogak.spring.web.dto.userdto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRequestDto {
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CheckNicknameDto {
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하입니다.")
        private String nickname;
    }

    @Builder
    @Getter
    public static class CreateUserDto {
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하입니다.")
        private String nickname;
        @Size(min = 1, max = 100)
        private String job;
        @Size(min = 1, max = 100)
        private String address;
//        @Size(min = 1, max = 100)
//        private String email;
        private Long userId;
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
        @Size(min = 2, max = 10, message = "닉네임은 최대 10자입니다.")
        private String nickname;
    }

    @Getter
    public static class UpdateJobDto {
        @Size(min = 1, max = 100)
        private String job;
    }

    @Builder
    @Getter
    public static class UpdateImageDto {
        private String imgName;
        private String imgUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetEmailDto {
        private String email;
    }
}
