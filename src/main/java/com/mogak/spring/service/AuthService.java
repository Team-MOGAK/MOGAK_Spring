package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.redis.RedisService;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String LOGOUT_ACCESS_TOKEN_PREFIX = "logout";

    @Value("${jwt.access-token-expiry}")
    private Long accessTokenExpiry;
    @Value("${jwt.refresh-token-expiry}")
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
                    .userId(savedUser.getId())
                    .tokens(jwtTokens)
                    .build();
        }
        //회원가입이 되어 있는 경우
        User findUser = userRepository.findByEmail(appleUser.getEmail()).get();
        JwtTokens jwtTokens = issueTokens(findUser); //토큰 발급
        storeRefresh(appleUser.getEmail(), jwtTokens); //리프레시 토큰 저장
        return AppleLoginResponse.builder()
                .isRegistered(true)
                .userId(findUser.getId())
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

    private void storeRefresh(String email, JwtTokens jwtTokens){
        redisService.setValues(
                email,
                "${jwtTokens.refreshToken}",

                refreshTokenExpiry
        );
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public JwtTokens reissue(String refreshToken){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User findUser = userRepository.findByEmail(email).get();
        JwtTokens jwtTokens = jwtTokenProvider.refresh(refreshToken, findUser.getId(), email);
        storeRefresh( email, jwtTokens);
        redisService.deleteValues(email);
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

    @Transactional
    public void logout(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        //refresh 삭제
        //access저장


    }


    /**
     * 로그인한 사용자 탈퇴
     */
    @Transactional
    public boolean deleteUser(Long userId){
        User deleteUser = userRepository.findById(userId).get();
        deleteUser.updateValidation("INACTIVE");
        userRepository.deleteById(deleteUser.getId()); //user soft delete
        /*
            추가로 user의 모다라트, 모각, 조각, 회고록, 댓글 삭제
         */
        redisService.deleteValues(deleteUser.getEmail());
        return true;
    }
}
