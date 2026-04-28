package com.mogak.spring.jwt;

public record JwtTokens(
        String accessToken,
        String refreshToken
) {
}
