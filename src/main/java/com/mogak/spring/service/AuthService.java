package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.redis.RedisService;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.util.Regex;
import com.mogak.spring.web.dto.AppleLoginRequest;
import com.mogak.spring.web.dto.AppleLoginResponse;
import com.mogak.spring.web.dto.AppleSignUpRequest;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Member;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    //로그인, 토큰, 로그아웃, 회원탈퇴

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final AddressRepository addressRepository;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private static final String REFRESH_TOKEN_PREFIX = "refresh";
    private static final String LOGOUT_ACCESS_TOKEN_PREFIX = "logout";

    @Value("${jwt.access-token-expiry")
    private Long accessTokenExpiry;
    @Value("${jwt.refresh-token-expiry")
    private Long refreshTokenExpiry;

    //로그인
    @Transactional
    public AppleLoginResponse appleLogin(AppleLoginRequest request) {

        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request.getId_token());
        boolean isRegistered = userRepository.existsByEmail(appleUser.getEmail());
        if (!isRegistered) { //회원가입이 되어 있지 않는 경우
            User oauthUser = new User(appleUser.getEmail());
            User savedUser = userRepository.save(oauthUser);
            JwtTokens jwtTokens = issueTokens(savedUser);
            return AppleLoginResponse.builder()
                    .isRegistered(false)
                    .tokens(jwtTokens)
                    .build();
        }
        //회원가입이 되어 있는 경우
        User findUser = userRepository.findByEmail(appleUser.getEmail()).get();
        JwtTokens jwtTokens = issueTokens(findUser); //토큰 발급
        storeRefresh(jwtTokens, appleUser.getEmail()); //리프레시 토큰 저장
        return AppleLoginResponse.builder()
                .isRegistered(true)
                .tokens(jwtTokens)
                .build();
    }

    /**
     * 토큰 발급
     */
    private JwtTokens issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    private void storeRefresh(JwtTokens jwtTokens, String email){
        redisService.setValues(
                "$REFRESH_TOKEN_PREFIX:${jwtTokens.refreshToken}",
                email,
                refreshTokenExpiry
        );
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public JwtTokens reissue(String refreshToken){
        String email = redisService.getValues("$REFRESH_TOKEN_PREFIX:${refreshToken}");
        User findUser = userRepository.findByEmail(email).get();
        JwtTokens jwtTokens = jwtTokenProvider.refresh(refreshToken, findUser.getId(), email);
        storeRefresh(jwtTokens, findUser.getEmail());
        redisService.deleteValues("$REFRESH_TOKEN_PREFIX:${refreshToken}");
        return jwtTokens;
    }

//    public String appleSignUp(AppleSignUpRequest request) {
//        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request.getId_token());
//        boolean isRegistered = userRepository.existsByEmail(appleUser.getEmail());
//        if (isRegistered) { //회원가입이 되어 있는 경우
//            User findUser = userRepository.findByEmail(appleUser.getEmail()).get();
//            String token = jwtService.issueToken(findUser); //토큰 발급
//            return token;
//        }
//        //create(reqeust,uploadImageDto); 이 부분은 userService에 있는 것으로 같이 합쳐야함
////        User user = User user = userRepository.findByEmail(appleUser.getEmail()).get();
////        String token = jwtService.issueToken(findUser);
//        //storeRefresh(token, appleUser.getEmail());
//        return token;
//    }

    /**
     * 로그인한 사용자 탈퇴
     */
    public boolean deleteUser(Long userId){
        User deleteUser = userRepository.findById(userId).get();
        deleteUser.updateValidation("INACTIVE");
        userRepository.deleteById(deleteUser.getId()); //user soft delete
        /*
            추가로 user의 모다라트, 모각, 조각, 회고록, 댓글 삭제
         */
//        redisService.deleteValues();
        return true;
    }
}
