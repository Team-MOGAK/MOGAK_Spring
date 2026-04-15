package com.mogak.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.jwt.JwtAuthenticationProvider;
import com.mogak.spring.jwt.JwtTokenCodec;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.security.ApiAccessDeniedHandler;
import com.mogak.spring.security.ApiAuthenticationEntryPoint;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.AuthService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.UserService;
import com.mogak.spring.web.controller.AuthController;
import com.mogak.spring.web.controller.UserController;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({UserController.class, AuthController.class})
@Import({
        SecurityConfig.class,
        JwtAuthenticationProvider.class,
        ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class,
        JwtTokenCodec.class,
        GlobalExceptionHandler.class
})
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenCodec jwtTokenCodec;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private StorageService storageService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("보호 API는 토큰 없이 호출하면 401을 반환한다")
    void protectedApiRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header("Origin", "http://localhost")
                        .with(request -> {
                            request.setServerName("api.localhost");
                            return request;
                        }))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost"))
                .andExpect(jsonPath("$.code").value("T003"));
    }

    @Test
    @DisplayName("보호 API는 만료 access token이면 T002를 반환한다")
    void protectedApiRejectsExpiredTokenWithExpireTokenCode() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header(JwtTokenProvider.access_header, "Bearer " + expiredAccessToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("T002"));
    }

    @Test
    @DisplayName("보호 API는 잘못된 access token이면 T001을 반환한다")
    void protectedApiRejectsMalformedTokenWithWrongTokenCode() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header(JwtTokenProvider.access_header, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("T001"));
    }

    @Test
    @DisplayName("refresh API는 만료 access header가 있어도 refresh token 처리까지 도달한다")
    void refreshAllowsExpiredAccessTokenHeader() throws Exception {
        when(authService.reissue("valid-refresh-token"))
                .thenReturn(JwtTokens.builder()
                        .accessToken("new-access-token")
                        .refreshToken("new-refresh-token")
                        .build());

        mockMvc.perform(post("/api/auth/refresh")
                        .header(JwtTokenProvider.access_header, "Bearer " + expiredAccessToken())
                        .header(JwtTokenProvider.refresh_header, "valid-refresh-token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.result.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("ROLE_USER access token은 회원 등록 API에 접근할 수 없다")
    void joinRejectsUserRole() throws Exception {
        mockMvc.perform(joinRequest(SecurityAuthority.USER.getAuthority())
                        .header("Origin", "http://localhost")
                        .with(request -> {
                            request.setServerName("api.localhost");
                            return request;
                        }))
                .andExpect(status().isForbidden())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost"))
                .andExpect(jsonPath("$.code").value("T004"));
    }

    @Test
    @DisplayName("ROLE_PENDING access token은 회원 등록 API에 접근할 수 있다")
    void joinAllowsPendingRole() throws Exception {
        when(userService.create(anyLong(), any(UserRequestDto.CreateUserDto.class), any(UserRequestDto.UploadImageDto.class)))
                .thenReturn(UserResponseDto.CreateDto.builder()
                        .userId(10L)
                        .nickname("tester")
                        .tokens(JwtTokens.builder()
                                .accessToken("access-token")
                                .refreshToken("refresh-token")
                                .build())
                        .build());

        mockMvc.perform(joinRequest(SecurityAuthority.PENDING.getAuthority()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.userId").value(10L))
                .andExpect(jsonPath("$.result.tokens.accessToken").value("access-token"));
    }

    private org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder joinRequest(String role)
            throws Exception {
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(UserRequestDto.CreateUserDto.builder()
                        .userId(10L)
                        .nickname("tester")
                        .job("개발/데이터")
                        .address("서울특별시")
                        .build())
        );

        return multipart("/api/users/join")
                .file(request)
                .header(JwtTokenProvider.access_header, "Bearer " + accessToken(role));
    }

    private String accessToken(String role) {
        Instant now = Instant.now();
        return jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", 10L)
                .claim("email", "user@test.com")
                .claim("role", role)
                .claim("token_type", JwtTokenProvider.ACCESS_TOKEN_TYPE)
                .subject("user@test.com")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build());
    }

    private String expiredAccessToken() {
        Instant now = Instant.now();
        return jwtTokenCodec.encode(JwtClaimsSet.builder()
                .claim("id", 10L)
                .claim("email", "user@test.com")
                .claim("role", SecurityAuthority.USER.getAuthority())
                .claim("token_type", JwtTokenProvider.ACCESS_TOKEN_TYPE)
                .subject("user@test.com")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(now.minusSeconds(60))
                .build());
    }
}
