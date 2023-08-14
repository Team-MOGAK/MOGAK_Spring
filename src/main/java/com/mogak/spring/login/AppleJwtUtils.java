package com.mogak.spring.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleJwtUtils extends JwtUtils {
    private final AppleClient appleClient;

    @Override
    public Claims getClaimsBy(String identityToken)

	try {
        ApplePublicKeyResponse response = appleClient.getAppleAuthPublicKey();

        String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
        Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
        ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

        byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
        KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();

    } catch (
    NoSuchAlgorithmException e) {
    } catch (
    InvalidKeySpecException e) {
    } catch (
    SignatureException e | MalformedJwtException e) {
        //토큰 서명 검증 or 구조 문제 (Invalid token)
    } catch (ExpiredJwtException e) {
        //토큰이 만료됐기 때문에 클라이언트는 토큰을 refresh 해야함.
    } catch (Exception e) {
    }
}
