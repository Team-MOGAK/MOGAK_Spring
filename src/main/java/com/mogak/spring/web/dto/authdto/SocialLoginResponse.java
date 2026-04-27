package com.mogak.spring.web.dto.authdto;

import com.mogak.spring.jwt.JwtTokens;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "소셜 로그인 응답")
public class SocialLoginResponse {
    @Schema(description = "닉네임 등록이 완료된 회원이면 true, 추가 회원 정보 입력이 필요하면 false", example = "false")
    private Boolean isRegistered;
    @Schema(description = "로그인한 사용자 ID. JWT subject와 id claim에도 같은 값이 들어간다.", example = "10")
    private Long userId;
    @Schema(description = "발급된 access token과 refresh token")
    private JwtTokens tokens;
}
