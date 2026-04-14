package com.mogak.spring.login;

import com.mogak.spring.jwt.JwtTokenCodec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenHandlerTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("mogak-test-jwt-secret-for-hs256!".getBytes(StandardCharsets.UTF_8));

    private final JwtTokenHandler jwtTokenHandler = new JwtTokenHandler(new JwtTokenCodec(SECRET));

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("레거시 JwtTokenHandler는 새 표준 id claim에서 userPk를 읽는다")
    void getUserPkReadsIdClaim() {
        String token = jwtTokenHandler.createJwtToken("10");

        assertThat(jwtTokenHandler.getUserPk(token)).isEqualTo("10");
    }

    @Test
    @DisplayName("AuthHandler는 Authorization Bearer 토큰에서 user id를 복원한다")
    void authHandlerReadsUserIdFromBearerToken() {
        String token = jwtTokenHandler.createJwtToken("10");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtTokenHandler.AUTHORIZATION, "Bearer " + token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        AuthHandler authHandler = new AuthHandler(jwtTokenHandler);

        assertThat(authHandler.getUserId()).isEqualTo(10L);
    }
}
