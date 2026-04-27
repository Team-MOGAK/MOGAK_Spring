package com.mogak.spring.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GoogleClaimsValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_GOOGLE_AUDIENCE =
            new OAuth2Error("invalid_token", "Google OAuth audience is invalid", null);

    private final OAuth2TokenValidator<Jwt> issuerValidator;
    private final Set<String> clientIds;

    public GoogleClaimsValidator(
            @Value("${oauth.google.iss}") String issuer,
            @Value("${oauth.google.client-ids:${oauth.google.client-id}}") String clientIds
    ) {
        this.issuerValidator = JwtValidators.createDefaultWithIssuer(issuer);
        this.clientIds = parseClientIds(clientIds);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt claims) {
        OAuth2TokenValidatorResult issuerValidationResult = issuerValidator.validate(claims);
        if (issuerValidationResult.hasErrors()) {
            return issuerValidationResult;
        }
        if (claims.getAudience().stream().noneMatch(clientIds::contains)) {
            return OAuth2TokenValidatorResult.failure(INVALID_GOOGLE_AUDIENCE);
        }
        return OAuth2TokenValidatorResult.success();
    }

    private Set<String> parseClientIds(String clientIds) {
        return Arrays.stream(clientIds.split(","))
                .map(String::trim)
                .filter(clientId -> !clientId.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }
}
