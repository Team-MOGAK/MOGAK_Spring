package com.mogak.spring.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-user-client", url = "${oauth.kakao.user-info-uri}")
public interface KakaoClient {

    @GetMapping("/v2/user/me")
    KakaoUserResponse getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization);
}
