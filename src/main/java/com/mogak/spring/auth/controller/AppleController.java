package com.mogak.spring.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppleController {


    @PostMapping("/login")
    public String handleAppleLogin(@RequestBody  OAuth2AuthenticationToken authenticationToken) {



        return null; // 로그인 후 홈페이지로 리디렉션
    }

}
