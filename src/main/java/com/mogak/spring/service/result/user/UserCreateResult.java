package com.mogak.spring.service.result.user;

import com.mogak.spring.jwt.JwtTokens;

public record UserCreateResult(Long userId, String nickname, JwtTokens tokens) {
}
