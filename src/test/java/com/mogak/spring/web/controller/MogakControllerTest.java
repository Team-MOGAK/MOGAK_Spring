package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MogakController.class)
@ActiveProfiles("test")
class MogakControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private MogakService mogakService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @BeforeEach
    void setUp() {
        SecurityContextTestHelper.setAuthentication("user@test.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("모각 생성 요청이 성공하면 생성 응답 계약을 반환한다")
    void createMogakContract() throws Exception {
        when(mogakService.create(anyLong(), any(MogakRequestDto.CreateDto.class))).thenReturn(MogakResponseDto.GetMogakDto.builder()
                .id(1L)
                .title("정보처리기사")
                .bigCategory(MogakCategory.builder().id(1).name("자격증").build())
                .smallCategory("필기")
                .color("#112233")
                .build());

        mockMvc.perform(post("/api/modarats/mogaks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MogakRequestDto.CreateDto.builder()
                                .modaratId(10L)
                                .title("정보처리기사")
                                .bigCategory("자격증")
                                .smallCategory("필기")
                                .color("#112233")
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value("정보처리기사"))
                .andExpect(jsonPath("$.result.smallCategory").value("필기"));

        verify(mogakService).create(eq(1L), any(MogakRequestDto.CreateDto.class));
    }

    @Test
    @DisplayName("모각 생성 요청이 유효하지 않으면 에러 응답 계약을 반환한다")
    void createMogakValidationErrorContract() throws Exception {
        mockMvc.perform(post("/api/modarats/mogaks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"정보처리기사\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("Z005"))
                .andExpect(jsonPath("$.message").value("입력값이 유효하지 않습니다"));
    }

    @Test
    @DisplayName("모각 목록 조회 요청이 성공하면 목록 응답 계약을 반환한다")
    void getMogakListContract() throws Exception {
        when(mogakService.getMogakDtoList(anyLong(), eq(10L))).thenReturn(MogakResponseDto.GetMogakListDto.builder()
                .size(1)
                .mogaks(List.of(MogakResponseDto.GetMogakDto.builder()
                        .id(1L)
                        .title("정보처리기사")
                        .bigCategory(MogakCategory.builder().id(1).name("자격증").build())
                        .smallCategory("필기")
                        .color("#112233")
                        .build()))
                .build());

        mockMvc.perform(get("/api/modarats/10/mogaks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.size").value(1))
                .andExpect(jsonPath("$.result.mogaks[0].title").value("정보처리기사"));

        verify(mogakService).getMogakDtoList(1L, 10L);
    }

    @Test
    @DisplayName("조각 목록 조회 요청이 성공하면 목록 응답 계약을 반환한다")
    void getJogaksContract() throws Exception {
        when(mogakService.getJogaks(anyLong(), eq(1L), eq(LocalDate.of(2026, 3, 26)))).thenReturn(List.of(
                JogakResponseDto.GetJogakDto.builder()
                        .jogakId(100L)
                        .mogakTitle("정보처리기사")
                        .category("자격증")
                        .title("문제풀이")
                        .isRoutine(false)
                        .isAlreadyAdded(true)
                        .achievements(1)
                        .startDate(LocalDate.of(2026, 3, 26))
                        .build()
        ));

        mockMvc.perform(get("/api/modarats/mogaks/1/jogaks")
                        .param("date", "2026-03-26"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].jogakId").value(100L))
                .andExpect(jsonPath("$.result[0].isAlreadyAdded").value(true));

        verify(mogakService).getJogaks(1L, 1L, LocalDate.of(2026, 3, 26));
    }

    @Test
    @DisplayName("모각 삭제 요청이 성공하면 성공 응답 계약을 반환한다")
    void deleteMogakContract() throws Exception {
        mockMvc.perform(delete("/api/modarats/mogaks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());

        verify(mogakService).deleteMogak(1L, 1L);
    }

    @Test
    @DisplayName("모각 수정 요청이 성공하면 userId를 서비스로 전달한다")
    void updateMogakForwardsUserId() throws Exception {
        when(mogakService.updateMogak(eq(1L), any(MogakRequestDto.UpdateDto.class))).thenReturn(MogakResponseDto.GetMogakDto.builder()
                .id(1L)
                .title("수정된 모각")
                .bigCategory(MogakCategory.builder().id(1).name("자격증").build())
                .smallCategory("필기")
                .color("#112233")
                .build());

        mockMvc.perform(put("/api/modarats/mogaks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MogakRequestDto.UpdateDto.builder()
                                .mogakId(1L)
                                .title("수정된 모각")
                                .bigCategory("자격증")
                                .smallCategory("필기")
                                .color("#112233")
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value("수정된 모각"));

        verify(mogakService).updateMogak(eq(1L), any(MogakRequestDto.UpdateDto.class));
    }

    @Test
    @DisplayName("존재하지 않는 모각 삭제를 요청하면 에러 응답 계약을 반환한다")
    void deleteMogakNotFoundContract() throws Exception {
        doThrow(new MogakException(ErrorCode.NOT_EXIST_MOGAK))
                .when(mogakService).deleteMogak(1L, 99L);

        mockMvc.perform(delete("/api/modarats/mogaks/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("M004"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 모각입니다"));
    }
}
