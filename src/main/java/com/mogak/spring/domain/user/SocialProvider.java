package com.mogak.spring.domain.user;

import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;

import java.util.Locale;

public enum SocialProvider {
    APPLE,
    GOOGLE,
    KAKAO;

    public static SocialProvider from(String value) {
        try {
            return SocialProvider.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BaseException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }
    }
}
