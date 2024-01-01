package com.mogak.spring.auth;

import com.mogak.spring.auth.ApplePublicKeys;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-public-key-client", url = "https://appleid.apple.com/auth")
public interface AppleClient {

    /*
    apple 서버와 통신하여 public key를 얻기 위함
     */
    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}
