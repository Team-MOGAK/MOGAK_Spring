package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.AppleLoginRequest;
import com.mogak.spring.web.dto.AppleLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AppleOAuthUserProvider appleOAuthUserProvider;

    public AppleLoginResponse appleLogin(AppleLoginRequest request){
        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request)
    }
}
