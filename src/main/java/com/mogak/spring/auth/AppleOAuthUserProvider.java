package com.mogak.spring.auth;

import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.domain.user.SocialProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

/*
유저 정보 반환
 */
@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider implements SocialOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public AppleUserResponse getAppleUser(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Jwt claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return new AppleUserResponse(
                claims.getSubject(),
                claims.getClaimAsString("email"),
                resolveEmailVerified(claims.getClaim("email_verified"))
        );
    }

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.APPLE == provider;
    }

    @Override
    public SocialUserProfile getUser(String token) {
        AppleUserResponse appleUser = getAppleUser(token);
        return new SocialUserProfile(
                SocialProvider.APPLE,
                appleUser.providerUserId(),
                appleUser.email(),
                appleUser.emailVerified()
        );
    }

    private void validateClaims(Jwt claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new BaseException(ErrorCode.NOT_VALID_APPLE_CLAIMS);
        }
    }

    private Boolean resolveEmailVerified(Object emailVerified) {
        if (emailVerified instanceof Boolean value) {
            return value;
        }
        if (emailVerified instanceof String value) {
            return Boolean.parseBoolean(value);
        }
        return false;
    }

}
