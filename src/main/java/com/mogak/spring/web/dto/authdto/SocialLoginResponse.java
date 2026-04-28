package com.mogak.spring.web.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.jwt.JwtTokens;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 응답")
public record SocialLoginResponse(
        @JsonProperty("isRegistered")
        @Schema(description = "닉네임 등록이 완료된 회원이면 true, 추가 회원 정보 입력이 필요하면 false", example = "false")
        Boolean isRegistered,
        @Schema(description = "로그인한 사용자 ID. JWT subject와 id claim에도 같은 값이 들어간다.", example = "10")
        Long userId,
        @Schema(description = "발급된 access token과 refresh token")
        JwtTokens tokens
) {
}
