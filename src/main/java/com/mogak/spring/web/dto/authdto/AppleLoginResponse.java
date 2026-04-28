package com.mogak.spring.web.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.jwt.JwtTokens;

public record AppleLoginResponse(
        @JsonProperty("isRegistered") Boolean isRegistered, //등록된 유저인지
        Long userId,
        JwtTokens tokens //access&refresh
) {
}
