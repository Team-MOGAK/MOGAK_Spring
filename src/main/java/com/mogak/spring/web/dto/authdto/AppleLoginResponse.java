package com.mogak.spring.web.dto.authdto;

import com.mogak.spring.jwt.JwtTokens;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class AppleLoginResponse {

    private Boolean isRegistered; //등록된 유저인지
    private Long userId;
    private JwtTokens tokens; //access&refresh
}
