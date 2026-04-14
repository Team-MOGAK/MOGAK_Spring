package com.mogak.spring.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/*
claims 검증(id token 파싱 후)
 */
@Component
public class AppleClaimsValidator {

    private final String iss;
    private final String clientId;

    public AppleClaimsValidator(
            @Value("${oauth.apple.iss}") String iss,
            @Value("${oauth.apple.client-id}") String clientId
    ) {
        this.iss = iss;
        this.clientId = clientId;
    }

    public boolean isValid(Jwt claims) {
        String issuer = claims.getClaimAsString("iss");
        return issuer != null &&
                iss.equals(issuer) &&
                claims.getAudience().contains(clientId);
    }
}
