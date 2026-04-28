package com.mogak.spring.web.dto.userdto;

import jakarta.validation.constraints.Size;

public record UserUpdateNicknameRequest(
        @Size(min = 2, max = 10, message = "닉네임은 최대 10자입니다.")
        String nickname
) {
}
