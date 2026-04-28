package com.mogak.spring.web.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하입니다.")
        String nickname,
        @Size(min = 1, max = 100)
        String job,
        @Size(min = 1, max = 100)
        String address
) {
}
