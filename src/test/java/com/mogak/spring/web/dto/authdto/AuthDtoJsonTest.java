package com.mogak.spring.web.dto.authdto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.jwt.JwtTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthDtoJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @DisplayName("AppleLoginRequest는 id_token JSON 이름을 유지한다")
    void appleLoginRequestUsesSnakeCaseTokenField() throws Exception {
        AppleLoginRequest request = new AppleLoginRequest("apple-id-token");

        String json = objectMapper.writeValueAsString(request);

        assertThat(json).contains("\"id_token\":\"apple-id-token\"");
        assertThat(objectMapper.readValue(json, AppleLoginRequest.class).idToken()).isEqualTo("apple-id-token");
    }

    @Test
    @DisplayName("AppleLoginResponse는 isRegistered와 tokens JSON 이름을 유지한다")
    void appleLoginResponseUsesExpectedFieldNames() throws Exception {
        AppleLoginResponse response = new AppleLoginResponse(true, 10L, new JwtTokens("access-token", "refresh-token"));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"isRegistered\":true");
        assertThat(json).contains("\"userId\":10");
        assertThat(json).contains("\"tokens\":{\"accessToken\":\"access-token\",\"refreshToken\":\"refresh-token\"}");
    }

    @Test
    @DisplayName("WithdrawDto는 isDeleted JSON 이름을 유지한다")
    void withdrawDtoUsesExpectedFieldName() throws Exception {
        AuthResponse.WithdrawDto response = new AuthResponse.WithdrawDto(true);

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"isDeleted\":true");
        assertThat(objectMapper.readValue(json, AuthResponse.WithdrawDto.class).isDeleted()).isTrue();
    }
}
