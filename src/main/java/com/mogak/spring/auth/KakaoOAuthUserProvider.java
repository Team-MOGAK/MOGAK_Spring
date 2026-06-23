package com.mogak.spring.auth;

import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthUserProvider implements SocialOAuthUserProvider {
    private static final String BEARER_PREFIX = "Bearer ";

    private final KakaoClient kakaoClient;

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.KAKAO == provider;
    }

    @Override
    public SocialUserProfile getUser(String token) {
        try {
            KakaoUserResponse response = kakaoClient.getUser(BEARER_PREFIX + token);
            Long providerUserId = response.id();
            if (providerUserId == null) {
                throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
            }
            String email = response.email();
            return new SocialUserProfile(SocialProvider.KAKAO, String.valueOf(providerUserId), email, response.emailVerified());
        } catch (BaseException e) {
            throw e;
        } catch (FeignException | IllegalArgumentException e) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }
}
