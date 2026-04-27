package com.mogak.spring.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "JWT 토큰 쌍")
public class JwtTokens {

    @Schema(description = "API 인증에 사용하는 access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    public String accessToken;
    @Schema(description = "토큰 재발급에 사용하는 refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
    public String refreshToken;
}
