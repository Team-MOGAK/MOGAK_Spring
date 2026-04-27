package com.mogak.spring.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/*
claims 검증(id token 파싱 후)
 */
@Component
public class AppleClaimsValidator {

    private final String iss;
    private final Set<String> clientIds;

    public AppleClaimsValidator(
            @Value("${oauth.apple.iss}") String iss,
            @Value("${oauth.apple.client-ids:${oauth.apple.client-id}}") String clientIds
    ) {
        this.iss = iss;
        this.clientIds = parseClientIds(clientIds);
    }

    public boolean isValid(Jwt claims) {
        String issuer = claims.getClaimAsString("iss");
        return iss.equals(issuer) &&
                claims.getAudience().stream().anyMatch(clientIds::contains);
    }

    private Set<String> parseClientIds(String clientIds) {
        return Arrays.stream(clientIds.split(","))
                .map(String::trim)
                .filter(clientId -> !clientId.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }
}
