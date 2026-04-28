package com.mogak.spring.service.result;

import com.mogak.spring.jwt.JwtTokens;

public record SocialLoginResult(boolean isRegistered, Long userId, JwtTokens tokens) {
}
