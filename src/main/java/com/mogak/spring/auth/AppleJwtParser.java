package com.mogak.spring.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.global.ErrorCode;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.security.PublicKey;
import java.util.Map;
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
            String decodedHeader = new String(Base64Utils.decodeFromUrlSafeString(encodedHeader));
            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) { //Token header가 올바르지 않으면 예외발생
            throw new AuthException(ErrorCode.INVALID_APPLE_ID_TOKEN);
        }
    }
    /*
    id token 파싱
     */
    public Claims parsePublicKeyAndGetClaims(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.EXPIRE_APPLE_ID_TOKEN);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new AuthException(ErrorCode.INVALID_APPLE_ID_TOKEN);//예외처리 수정필요
        }
    }
}

