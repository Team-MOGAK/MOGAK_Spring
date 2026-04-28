package com.mogak.spring.jwt;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 토큰 쌍")
public record JwtTokens(
        @Schema(description = "API 인증에 사용하는 access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "토큰 재발급에 사용하는 refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken
) {
}
