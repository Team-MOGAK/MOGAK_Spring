package com.mogak.spring.auth;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;

    public CustomRequestEntityConverter() {
        defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    }

    @Value("${security.oauth2.client.registration.apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${security.oauth2.client.registration.apple.key-id}")
    private String APPLE_LOGIN_KEY;

    @Value("${security.oauth2.client.registration.apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${security.oauth2.client.registration.apple.redirect-uri}")
    private String APPLE_REDIRECT_URL;

    @Value("${security.oauth2.client.registration.apple.key-id}")
    private String APPLE_KEY_ID;

    @Value("${security.oauth2.client.registration.apple.key-path}")
    private String APPLE_KEY_PATH;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest value) {
        return null;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return null;
    }

    /*
    **  임시로 authorization code 받아옴
     */
    public String getAppleLogin() {
        return APPLE_AUTH_URL + "/auth/authorize"
                + "?client_id=" + APPLE_CLIENT_ID
                + "&redirect_uri=" + APPLE_REDIRECT_URL
                + "&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }


    /*
     ** client-secret을 생성하기 위한 private key
     */
    public String createClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", APPLE_KEY_ID);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(APPLE_TEAM_ID)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(expirationDate) // 만료 시간
                .setAudience(APPLE_AUTH_URL)
                .setSubject(APPLE_CLIENT_ID)
                .signWith(getPrivateKey())
                .compact();
    }
    /*
     **  private key를 활용해 client_secret 생성 - JWT로 생성
     */
    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(APPLE_KEY_PATH);
        // 배포시 jar 파일을 찾지 못함
        //String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));

//        InputStream in = resource.getInputStream();
//        PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
//        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
//        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return null;
    }


}
