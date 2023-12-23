package com.mogak.spring.jwt;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class JwtTokens {

    public String accessToken;
    public String refreshToken;
}
