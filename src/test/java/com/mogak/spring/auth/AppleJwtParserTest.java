package com.mogak.spring.auth;

import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.support.ErrorCodeAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AppleJwtParserTest {
    private final AppleJwtParser appleJwtParser = new AppleJwtParser();

    @Test
    @DisplayName("Apple identity token으로 헤더를 파싱한다")
    void parseHeaders() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        String identityToken = createIdentityToken(keyPair, Instant.now().plusSeconds(60 * 60 * 24));

        Map<String, String> actual = appleJwtParser.parseHeaders(identityToken);

        assertThat(actual).containsKeys("alg", "kid");
    }

    @Test
    @DisplayName("Apple identity token, PublicKey를 받아 사용자 정보가 포함된 Claims를 반환한다")
    void parsePublicKeyAndGetClaims() throws NoSuchAlgorithmException {
        String expected = "19281729";
        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        String identityToken = createIdentityToken(keyPair, expected, Instant.now().plusSeconds(60 * 60 * 24));

        Jwt claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);

        assertAll(
                () -> assertThat(claims.getClaims()).isNotEmpty(),
                () -> assertThat(claims.getSubject()).isEqualTo(expected)
        );
    }

    @Test
    @DisplayName("만료된 Apple identity token을 받으면 Claims 획득 시에 예외를 반환한다")
    void parseExpiredTokenAndGetClaims() throws NoSuchAlgorithmException {
        String expected = "19281729";
        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        String identityToken = createIdentityToken(keyPair, expected, Instant.now().minusSeconds(1));

        Throwable throwable = catchThrowable(() -> appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.EXPIRE_APPLE_ID_TOKEN);
    }

    @Test
    @DisplayName("서명이 다른 Apple identity token은 INVALID_APPLE_ID_TOKEN 예외를 반환한다")
    void parseInvalidSignatureTokenAndGetClaims() throws NoSuchAlgorithmException {
        KeyPair signingKeyPair = generateKeyPair();
        KeyPair verificationKeyPair = generateKeyPair();
        String identityToken = createIdentityToken(signingKeyPair, Instant.now().plusSeconds(60 * 60 * 24));

        Throwable throwable = catchThrowable(() -> appleJwtParser.parsePublicKeyAndGetClaims(identityToken, verificationKeyPair.getPublic()));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_APPLE_ID_TOKEN);
    }

    @Test
    @DisplayName("RSA public key가 아니면 WRONG_APPLE_PUBLIC_KEY 예외를 반환한다")
    void parseWithNonRsaPublicKey() throws NoSuchAlgorithmException {
        KeyPair rsaKeyPair = generateKeyPair();
        KeyPair ecKeyPair = KeyPairGenerator.getInstance("EC")
                .generateKeyPair();
        String identityToken = createIdentityToken(rsaKeyPair, Instant.now().plusSeconds(60 * 60 * 24));

        Throwable throwable = catchThrowable(() -> appleJwtParser.parsePublicKeyAndGetClaims(identityToken, ecKeyPair.getPublic()));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.WRONG_APPLE_PUBLIC_KEY);
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
    }

    private String createIdentityToken(KeyPair keyPair, Instant expiresAt) {
        return createIdentityToken(keyPair, "19281729", expiresAt);
    }

    private String createIdentityToken(KeyPair keyPair, String subject, Instant expiresAt) {
        Instant now = Instant.now();
        Instant issuedAt = expiresAt.isBefore(now) ? expiresAt.minusSeconds(60 * 60) : now;
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId("W2R4HXF3K")
                .build();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .claim("id", "12345678")
                .issuer("iss")
                .issuedAt(issuedAt)
                .audience(List.of("aud"))
                .subject(subject)
                .expiresAt(expiresAt)
                .build();

        return NimbusJwtEncoder.withKeyPair((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate())
                .algorithm(SignatureAlgorithm.RS256)
                .jwkPostProcessor(jwk -> jwk.keyID("W2R4HXF3K"))
                .build()
                .encode(JwtEncoderParameters.from(jwsHeader, claimsSet))
                .getTokenValue();
    }

}
