package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.UserService;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.UserCreateResult;
import com.mogak.spring.service.result.UserProfileResult;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.web.dto.userdto.UserCreateRequest;
import com.mogak.spring.web.dto.userdto.UserUpdateNicknameRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private StorageService storageService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("닉네임 검증 요청이 성공하면 성공 응답 계약을 반환한다")
    void verifyNicknameContract() throws Exception {
        mockMvc.perform(post("/api/users/nickname/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"tester\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    @DisplayName("닉네임 검증 요청이 유효하지 않으면 에러 응답 계약을 반환한다")
    void verifyNicknameValidationErrorContract() throws Exception {
        mockMvc.perform(post("/api/users/nickname/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("Z005"))
                .andExpect(jsonPath("$.message").value("입력값이 유효하지 않습니다"));
    }

    @Test
    @DisplayName("회원 가입 multipart 요청이 성공하면 생성 응답 계약을 반환한다")
    void createUserMultipartContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.PENDING.getAuthority());
        UserCreateResult response = new UserCreateResult(1L, "tester", null);
        when(userService.create(anyLong(), any(String.class), any(String.class), any(String.class), any(ProfileImageResult.class), anyList()))
                .thenReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"nickname\":\"tester\",\"job\":\"개발/데이터\",\"address\":\"서울특별시\"}".getBytes()
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );
        when(storageService.uploadProfileImg(any(), any())).thenReturn(new ProfileImageResult("profile.png", "https://cdn/profile.png"));

        ResultActions result = mockMvc.perform(multipart("/api/users/join")
                        .file(requestPart)
                        .file(image)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }));

        result.andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.userId").value(1L))
                .andExpect(jsonPath("$.result.nickname").value("tester"));
    }

    @Test
    @DisplayName("회원 가입 요청의 동의 목록은 서비스 생성 요청으로 전달된다")
    void createUserForwardsConsents() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.PENDING.getAuthority());
        UserCreateResult response = new UserCreateResult(1L, "tester", null);
        when(userService.create(anyLong(), any(String.class), any(String.class), any(String.class), any(ProfileImageResult.class), anyList()))
                .thenReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                """
                        {"nickname":"tester","job":"개발/데이터","address":"서울특별시","consents":[{"consentItemId":1,"agreed":true},{"consentItemId":2,"agreed":false}]}
                        """.getBytes()
        );

        mockMvc.perform(multipart("/api/users/join")
                        .file(requestPart)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isCreated());

        ArgumentCaptor<List<UserConsentCommand>> captor = ArgumentCaptor.captor();
        verify(userService).create(
                eq(1L),
                eq("tester"),
                eq("개발/데이터"),
                eq("서울특별시"),
                any(ProfileImageResult.class),
                captor.capture()
        );
        assertThat(captor.getValue()).containsExactly(
                new UserConsentCommand(1L, true),
                new UserConsentCommand(2L, false)
        );
    }

    @Test
    @DisplayName("회원 가입 요청의 nullable 동의 목록은 빈 목록으로 전달된다")
    void createUserForwardsEmptyConsentsWhenNullable() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.PENDING.getAuthority());
        UserCreateResult response = new UserCreateResult(1L, "tester", null);
        when(userService.create(anyLong(), any(String.class), any(String.class), any(String.class), any(ProfileImageResult.class), anyList()))
                .thenReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"nickname\":\"tester\",\"job\":\"개발/데이터\",\"address\":\"서울특별시\",\"consents\":null}".getBytes()
        );

        mockMvc.perform(multipart("/api/users/join")
                        .file(requestPart)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isCreated());

        ArgumentCaptor<List<UserConsentCommand>> captor = ArgumentCaptor.captor();
        verify(userService).create(
                eq(1L),
                eq("tester"),
                eq("개발/데이터"),
                eq("서울특별시"),
                any(ProfileImageResult.class),
                captor.capture()
        );
        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    @DisplayName("회원 가입 요청의 동의 목록에 null 항목이 있으면 400을 반환한다")
    void createUserRejectsNullConsentItem() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.PENDING.getAuthority());
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                """
                        {"nickname":"tester","job":"개발/데이터","address":"서울특별시","consents":[null]}
                        """.getBytes()
        );

        mockMvc.perform(multipart("/api/users/join")
                        .file(requestPart)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(userService, never()).create(anyLong(), any(String.class), any(String.class), any(String.class), any(ProfileImageResult.class), anyList());
    }

    @Test
    @DisplayName("이미지가 포함된 회원 가입 요청은 storage 비활성 상태에서 503 에러 응답 계약을 반환한다")
    void createUserMultipartStorageDisabledContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.PENDING.getAuthority());
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"nickname\":\"tester\",\"job\":\"개발/데이터\",\"address\":\"서울특별시\"}".getBytes()
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        doThrow(new com.mogak.spring.exception.BaseException(ErrorCode.STORAGE_DISABLED))
                .when(storageService).uploadProfileImg(any(), any());

        mockMvc.perform(multipart("/api/users/join")
                        .file(requestPart)
                        .file(image)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.code").value("Z006"))
                .andExpect(jsonPath("$.message").value("스토리지 기능이 비활성화되어 있습니다"));
    }

    @Test
    @DisplayName("이미지 업로드 후 회원 가입이 실패하면 업로드된 프로필 이미지를 삭제한다")
    void createUserDeletesUploadedProfileImageWhenRegistrationFails() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.PENDING.getAuthority());
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                """
                        {"nickname":"tester","job":"개발/데이터","address":"서울특별시","consents":[{"consentItemId":1,"agreed":true},{"consentItemId":1,"agreed":false}]}
                        """.getBytes()
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );
        when(storageService.uploadProfileImg(any(), any()))
                .thenReturn(new ProfileImageResult("uploaded-profile.png", "https://cdn/uploaded-profile.png"));
        when(userService.create(anyLong(), any(String.class), any(String.class), any(String.class), any(ProfileImageResult.class), anyList()))
                .thenThrow(new com.mogak.spring.exception.BaseException(ErrorCode.DUPLICATE_CONSENT_ITEM));

        mockMvc.perform(multipart("/api/users/join")
                        .file(requestPart)
                        .file(image)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("U016"));

        verify(storageService).deleteProfileImg("uploaded-profile.png");
    }

    @Test
    @DisplayName("프로필 조회 요청이 성공하면 조회 응답 계약을 반환한다")
    void getUserProfileContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.USER.getAuthority());
        when(userService.getUserProfile(1L)).thenReturn(new UserProfileResult("tester", "개발/데이터", "https://cdn/profile.png"));

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.nickname").value("tester"))
                .andExpect(jsonPath("$.result.job").value("개발/데이터"))
                .andExpect(jsonPath("$.result.imgUrl").value("https://cdn/profile.png"));
    }

    @Test
    @DisplayName("중복 닉네임으로 변경을 요청하면 에러 응답 계약을 반환한다")
    void updateNicknameErrorContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.USER.getAuthority());
        doThrow(new com.mogak.spring.exception.UserException(ErrorCode.ALREADY_EXIST_USER))
                .when(userService).updateNickname(anyLong(), any(String.class));

        mockMvc.perform(put("/api/users/profile/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateNicknameRequest("tester"))))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value("U006"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 유저입니다"));
    }

    @Test
    @DisplayName("프로필 이미지 변경 요청은 storage 비활성 상태에서 503 에러 응답 계약을 반환한다")
    void updateImageStorageDisabledContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(1L, "user@test.com", SecurityAuthority.USER.getAuthority());
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        doThrow(new com.mogak.spring.exception.BaseException(ErrorCode.STORAGE_DISABLED))
                .when(storageService).updateProfileImg(any(), any(), any());

        mockMvc.perform(multipart("/api/users/profile/image")
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.code").value("Z006"))
                .andExpect(jsonPath("$.message").value("스토리지 기능이 비활성화되어 있습니다"));
    }
}
