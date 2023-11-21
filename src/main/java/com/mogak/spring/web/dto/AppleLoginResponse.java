package com.mogak.spring.web.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class AppleLoginResponse {

    private Boolean isRegistered; //등록된 유저인지
    private String token; //jwttoken
}
