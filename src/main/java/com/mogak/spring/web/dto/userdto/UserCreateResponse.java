package com.mogak.spring.web.dto.userdto;

import com.mogak.spring.jwt.JwtTokens;

public record UserCreateResponse(Long userId, String nickname, JwtTokens tokens) {
}
