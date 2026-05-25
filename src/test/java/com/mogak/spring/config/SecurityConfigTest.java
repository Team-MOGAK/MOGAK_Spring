package com.mogak.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.jwt.JwtAuthenticationProvider;
import com.mogak.spring.jwt.JwtTokenCodec;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.security.ApiAccessDeniedHandler;
import com.mogak.spring.security.ApiAuthenticationEntryPoint;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.ConsentService;
import com.mogak.spring.service.result.ConsentItemResult;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.SocialLoginResult;
import com.mogak.spring.service.result.metadata.MetadataOptionResult;
import com.mogak.spring.service.result.UserCreateResult;
import com.mogak.spring.service.AuthService;
import com.mogak.spring.service.MetadataService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.UserService;
import com.mogak.spring.web.controller.AuthController;
import com.mogak.spring.web.controller.ConsentController;
import com.mogak.spring.web.controller.MetadataController;
import com.mogak.spring.web.controller.UserController;
import com.mogak.spring.web.dto.authdto.SocialLoginRequest;
import com.mogak.spring.web.dto.authdto.SocialLoginResponse;
import com.mogak.spring.web.dto.userdto.UserCreateRequest;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({UserController.class, AuthController.class, MetadataController.class, ConsentController.class})
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
    private MetadataService metadataService;
    @MockitoBean
    private ConsentService consentService;
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
                .thenReturn(new JwtTokens("new-access-token", "new-refresh-token"));

        mockMvc.perform(post("/api/auth/refresh")
                        .header(JwtTokenProvider.access_header, "Bearer " + expiredAccessToken())
                        .header(JwtTokenProvider.refresh_header, "valid-refresh-token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.result.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("공급자별 소셜 로그인 API는 토큰 없이 호출할 수 있다")
    void socialLoginIsPublic() throws Exception {
        when(authService.socialLogin(any(), any(String.class)))
                .thenReturn(new SocialLoginResult(false, 10L, new JwtTokens("access-token", "refresh-token")));

        mockMvc.perform(post("/api/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"google-id-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.userId").value(10L))
                .andExpect(jsonPath("$.result.tokens.accessToken").value("access-token"));

        verify(authService).socialLogin(
                eq(SocialProvider.GOOGLE),
                eq("google-id-token")
        );
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
        when(userService.create(anyLong(), any(String.class), any(String.class), any(String.class), any(ProfileImageResult.class), anyList()))
                .thenReturn(new UserCreateResult(10L, "tester", new JwtTokens("access-token", "refresh-token")));

        mockMvc.perform(joinRequest(SecurityAuthority.PENDING.getAuthority()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.userId").value(10L))
                .andExpect(jsonPath("$.result.tokens.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("메타데이터 조회 API는 토큰 없이 호출할 수 있다")
    void metadataJobsEndpointIsPublic() throws Exception {
        when(metadataService.getJobs()).thenReturn(List.of(new MetadataOptionResult("개발/데이터")));

        mockMvc.perform(get("/api/metadata/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.result[0].name").value("개발/데이터"));
    }

    @Test
    @DisplayName("동의 항목 조회 API는 토큰 없이 호출할 수 있다")
    void consentItemsEndpointIsPublic() throws Exception {
        when(consentService.getActiveConsentItems())
                .thenReturn(List.of(new ConsentItemResult(1L, "MARKETING", "마케팅 수신", null, false)));

        mockMvc.perform(get("/api/consents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.result[0].code").value("MARKETING"));
    }

    private org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder joinRequest(String role)
            throws Exception {
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(new UserCreateRequest("tester", "개발/데이터", "서울특별시"))
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
                .subject("10")
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
                .subject("10")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(now.minusSeconds(60))
                .build());
    }
}
