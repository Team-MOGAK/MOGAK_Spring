package com.mogak.spring.auth;

import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuthUserProvider implements SocialOAuthUserProvider {

    private final JwtDecoder jwtDecoder;

    @Autowired
    public GoogleOAuthUserProvider(
            @Value("${oauth.google.jwk-set-uri}") String jwkSetUri,
            GoogleClaimsValidator googleClaimsValidator
    ) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(googleClaimsValidator);
        this.jwtDecoder = decoder;
    }

    GoogleOAuthUserProvider(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.GOOGLE == provider;
    }

    @Override
    public SocialUserProfile getUser(String token) {
        try {
            Jwt claims = jwtDecoder.decode(token);
            String email = claims.getClaimAsString("email");
            if (email == null || email.isBlank()) {
                throw new BaseException(ErrorCode.SOCIAL_EMAIL_REQUIRED);
            }
            String providerUserId = claims.getSubject();
            if (providerUserId == null || providerUserId.isBlank()) {
                throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
            }
            return new SocialUserProfile(
                    SocialProvider.GOOGLE,
                    claims.getSubject(),
                    email,
                    resolveEmailVerified(claims.getClaim("email_verified"))
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
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
