package com.mogak.spring.web.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
    public record CheckNicknameDto(
            @NotBlank(message = "닉네임을 입력해주세요.")
            @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하입니다.")
            String nickname
    ) {
    }

    public record CreateUserDto(
            @NotBlank(message = "닉네임을 입력해주세요.")
            @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하입니다.")
            String nickname,
            @Size(min = 1, max = 100)
            String job,
            @Size(min = 1, max = 100)
            String address
    ) {
    }

    public record UploadImageDto(String imgName, String imgUrl) {
    }

    public record UpdateUserDto(String contents) {
    }

    public record UpdateNicknameDto(
            @Size(min = 2, max = 10, message = "닉네임은 최대 10자입니다.")
            String nickname
    ) {
    }

    public record UpdateJobDto(
            @Size(min = 1, max = 100)
            String job
    ) {
    }

    public record UpdateImageDto(String imgName, String imgUrl) {
    }

    public record GetEmailDto(String email) {
    }
}
