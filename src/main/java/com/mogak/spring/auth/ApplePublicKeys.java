package com.mogak.spring.auth;

import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;

import java.util.List;
import java.util.Objects;

public record ApplePublicKeys(
        List<ApplePublicKey> keys
) {

    public ApplePublicKey getMatchesKey(String alg, String kid) {
        return keys()
                .stream()
                .filter(k -> Objects.equals(k.alg(), alg) && Objects.equals(k.kid(), kid))
                .findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode.APPLE_JWT_WRONG_TOKEN));
    }
}
