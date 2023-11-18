package com.mogak.spring.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

/*
유저 정보 반환
 */
@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public AppleUserResponse getAppleUser(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return new AppleUserResponse(claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new IllegalStateException("Apple OAuth Claims 값이 올바르지 않습니다."); //예외처리 수정 필요
        }
    }



}
