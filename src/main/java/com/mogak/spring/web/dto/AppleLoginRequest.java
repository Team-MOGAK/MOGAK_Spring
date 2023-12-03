package com.mogak.spring.web.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class AppleLoginRequest {
    private String id_token;
}
