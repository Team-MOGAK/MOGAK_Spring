package com.mogak.spring.service.result.auth;

import com.mogak.spring.jwt.JwtTokens;

public record SocialLoginResult(Boolean isRegistered, Long userId, JwtTokens tokens) {
}
