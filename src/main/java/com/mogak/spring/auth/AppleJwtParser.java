package com.mogak.spring.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/*
identy token에서 alg, kid 추출 -> id_token를 public key로 파싱
 */
@Component
public class AppleJwtParser {

    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /*
    identy token에서 alg, kid 추출
     */
    public Map<String, String> parseHeaders(String identityToken) {
        try {
            String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return OBJECT_MAPPER.readValue(decodedHeader, new TypeReference<>() {
            });
        } catch (JsonProcessingException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) { //Token header가 올바르지 않으면 예외발생
            throw new BaseException(ErrorCode.INVALID_APPLE_ID_TOKEN);
        }
    }

    /*
    id token 파싱
     */
    public Jwt parsePublicKeyAndGetClaims(String idToken, PublicKey publicKey) {
        if (!(publicKey instanceof RSAPublicKey rsaPublicKey)) {
            throw new BaseException(ErrorCode.WRONG_APPLE_PUBLIC_KEY);
        }
        try {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey)
                    .signatureAlgorithm(SignatureAlgorithm.RS256)
                    .validateType(false)
                    .build();
            decoder.setJwtValidator(new JwtTimestampValidator(Duration.ZERO));
            return decoder.decode(idToken);
        } catch (JwtValidationException e) {
            if (isExpired(e)) {
                throw new BaseException(ErrorCode.EXPIRE_APPLE_ID_TOKEN);
            }
            throw new BaseException(ErrorCode.INVALID_APPLE_ID_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(ErrorCode.INVALID_APPLE_ID_TOKEN);
        }
    }

    private boolean isExpired(JwtValidationException exception) {
        return exception.getErrors()
                .stream()
                .map(OAuth2Error::getDescription)
                .filter(Objects::nonNull)
                .map(description -> description.toLowerCase(Locale.ROOT))
                .anyMatch(description -> description.contains("expired"));
    }
}
