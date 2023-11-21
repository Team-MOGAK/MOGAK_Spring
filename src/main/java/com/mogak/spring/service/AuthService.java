package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.AppleLoginRequest;
import com.mogak.spring.web.dto.AppleLoginResponse;
import com.mogak.spring.web.dto.AppleSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Member;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AppleOAuthUserProvider appleOAuthUserProvider;

    //로그인
    public AppleLoginResponse appleLogin(AppleLoginRequest request){

        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request.getId_token());
        boolean isRegistered = userRepository.existsByEmail(appleUser.getEmail());
        if(!isRegistered){ //회원가입이 되어 있지 않는 경우
            return AppleLoginResponse(false, JwtTokens(accessToken=request.getId_token()));
        }
        //회원가입이 되어 있는 경우
        User findUser = userRepository.findByEmail(appleUser.getEmail()).get();
        String token = jwtService.issueToken(findUser); //토큰 발급
        //storeRefresh(token, appleUser.getEmail()); 리프레시 토큰 저장
        return new AppleLoginResponse(true, token);

    }

    public String appleSignUp(AppleSignUpRequest request){
        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request.getId_token());
        boolean isRegistered = userRepository.existsByEmail(appleUser.getEmail());
        if(isRegistered){ //회원가입이 되어 있는 경우
            User findUser = userRepository.findByEmail(appleUser.getEmail()).get();
            String token = jwtService.issueToken(findUser); //토큰 발급
            return token;
        }
        //create(reqeust,uploadImageDto); 이 부분은 userService에 있는 것으로 같이 합쳐야함
//        User user = User user = userRepository.findByEmail(appleUser.getEmail()).get();
//        String token = jwtService.issueToken(findUser);
        //storeRefresh(token, appleUser.getEmail());
        return token;
    }
}
