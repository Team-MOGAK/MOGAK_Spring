package com.mogak.spring.web.dto.authdto;

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
@Schema(description = "소셜 로그인 요청")
public class SocialLoginRequest {
    @Schema(
            description = "클라이언트가 소셜 공급자에서 받은 토큰. apple/google은 id token, kakao는 access token을 전달한다.",
            example = "eyJraWQiOiJzb2NpYWwtdG9rZW4i..."
    )
    private String token;
}
