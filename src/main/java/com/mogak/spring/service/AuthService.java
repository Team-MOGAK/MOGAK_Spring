package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.auth.AppleUserResponse;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
import com.mogak.spring.web.dto.authdto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    //로그인, 토큰, 로그아웃, 회원탈퇴

    private final UserRepository userRepository;
    private final ModaratRepository modaratRepository;
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;
    private final DailyJogakRepository dailyJogakRepository;
    private final JogakPeriodRepository jogakPeriodRepository;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;

    //로그인
    @Transactional
    public AppleLoginResponse appleLogin(AppleLoginRequest request) {
        AppleUserResponse appleUser = appleOAuthUserProvider.getAppleUser(request.getId_token());
        boolean isRegistered = userRepository.existsByEmail(appleUser.getEmail());
        if (isRegistered) { //회원가입이 되어 있는 경우-이메일 체크
            User findUser = userRepository.findByEmail(appleUser.getEmail())
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
            JwtTokens jwtTokens = issueTokens(findUser); //토큰 발급
            // Redis-backed refresh token storage is intentionally disabled for now.
            // storeRefresh(appleUser.getEmail(), jwtTokens);
            if (!isRegisterNickname(appleUser.getEmail())) { //해당 이메일로 가입한 유저의 닉네임 없으면 회원가입하도록
                return AppleLoginResponse.builder()
                        .isRegistered(false)
                        .userId(findUser.getId())
                        .tokens(jwtTokens)
                        .build();
            }
            return AppleLoginResponse.builder()
                    .isRegistered(true)
                    .userId(findUser.getId())
                    .tokens(jwtTokens)
                    .build();
        }
        //회원가입이 되어 있지 않은 경우
        User oauthUser = new User(appleUser.getEmail());
        User savedUser = userRepository.save(oauthUser);
        JwtTokens jwtTokens = issueTokens(savedUser);
        return AppleLoginResponse.builder()
                .isRegistered(false)
                .userId(savedUser.getId())
                .tokens(jwtTokens)
                .build();
    }

    /**
     * 닉네임 등록 여부
     */
    private boolean isRegisterNickname(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        if (user.getNickname() != null && !user.getNickname().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 토큰 발급
     */
    private JwtTokens issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Transactional
    public JwtTokens reissue(String refreshToken) {
        String email = jwtTokenProvider.getEmailByRefresh(refreshToken);
        System.out.println(email);
        User findUser = userRepository.findByEmail(email).orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        validateStoredRefreshToken(findUser, refreshToken);

        JwtTokens jwtTokens = jwtTokenProvider.refresh(refreshToken, findUser.getId(), email);
        findUser.updateRefreshToken(jwtTokens.getRefreshToken());
        return jwtTokens;
    }


    @Transactional
    public void logout(String accessToken) {
        String email = resolveLogoutUserEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        user.clearRefreshToken();
    }

    /**
     * 로그인한 사용자 탈퇴
     */
    @Transactional
    public AuthResponse.WithdrawDto deleteUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User deleteUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if (deleteUser == null) {
            return AuthResponse.WithdrawDto.builder()
                    .isDeleted(false)
                    .build();
        }
        deleteUser.updateValidation("INACTIVE");
        /**
         * TODO cascade로 변경
         */
        deleteUserInfo(deleteUser);
        return AuthResponse.WithdrawDto.builder()
                .isDeleted(true)
                .build();
    }

    public void deleteUserInfo(User deleteUser) {
        Optional<List<Jogak>> optJogaks = jogakRepository.findAllByUserId(deleteUser.getId());
        if (optJogaks.isPresent()) {
            for (Jogak jogak : optJogaks.get()) {
                dailyJogakRepository.deleteAllByJogak(jogak);
                jogakPeriodRepository.deleteAllByJogakId(jogak.getId());
            }
        }
        jogakRepository.deleteByUserId(deleteUser.getId());
        mogakRepository.deleteByUserId(deleteUser.getId());
        modaratRepository.deleteByUserId(deleteUser.getId());
        userRepository.deleteById(deleteUser.getId());
        // redisService.deleteValues(deleteUser.getEmail());
    }

    private void validateStoredRefreshToken(User user, String refreshToken) {
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new BaseException(ErrorCode.WRONG_TOKEN);
        }
    }

    private String resolveLogoutUserEmail(String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            try {
                String email = jwtTokenProvider.getUserPk(token);
                if (email != null && !email.isBlank()) {
                    return email;
                }
            } catch (RuntimeException ignored) {
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }

        throw new BaseException(ErrorCode.EMPTY_TOKEN);
    }
}
