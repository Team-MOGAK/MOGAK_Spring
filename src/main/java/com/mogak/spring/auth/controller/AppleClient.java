package com.mogak.spring.auth.controller;

import com.mogak.spring.auth.ApplePublicKeys;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-public-key-client", url="https://appleid.apple.com/auth")
public class AppleClient {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys() {
        return null;
    }
}
