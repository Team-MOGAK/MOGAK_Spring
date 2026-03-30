package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtInterceptor;
import com.mogak.spring.jwt.JwtTokenFilter;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.login.AuthHandler;
import com.mogak.spring.redis.RedisService;
import com.mogak.spring.service.AwsS3Service;
import com.mogak.spring.service.UserService;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private AwsS3Service awsS3Service;
    @MockBean
    private AuthHandler authHandler;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtInterceptor jwtInterceptor;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;
    @MockBean
    private RedisService redisService;
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @BeforeEach
    void setUp() throws Exception {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
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
        UserResponseDto.CreateDto response = UserResponseDto.CreateDto.builder()
                .userId(1L)
                .nickname("tester")
                .build();
        when(userService.create(any(UserRequestDto.CreateUserDto.class), any(UserRequestDto.UploadImageDto.class)))
                .thenReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"userId\":1,\"nickname\":\"tester\",\"job\":\"개발/데이터\",\"address\":\"서울특별시\"}".getBytes()
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );
        when(awsS3Service.uploadProfileImg(any(), any())).thenReturn(UserRequestDto.UploadImageDto.builder()
                .imgName("profile.png")
                .imgUrl("https://cdn/profile.png")
                .build());

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
    @DisplayName("프로필 조회 요청이 성공하면 조회 응답 계약을 반환한다")
    void getUserProfileContract() throws Exception {
        when(userService.getUserProfile()).thenReturn(UserResponseDto.GetUserDto.builder()
                .nickname("tester")
                .job("개발/데이터")
                .imgUrl("https://cdn/profile.png")
                .build());

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
        doThrow(new com.mogak.spring.exception.UserException(ErrorCode.ALREADY_EXIST_USER))
                .when(userService).updateNickname(any(UserRequestDto.UpdateNicknameDto.class));

        mockMvc.perform(put("/api/users/profile/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequestDto.UpdateNicknameDto() {{
                            org.springframework.test.util.ReflectionTestUtils.setField(this, "nickname", "tester");
                        }})))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value("U006"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 유저입니다"));
    }
}
