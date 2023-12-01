package com.mogak.spring.web.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class AppleSignUpRequest {

    private String id_token;
    private String nickname;
    private String job;
    private String address;
}
