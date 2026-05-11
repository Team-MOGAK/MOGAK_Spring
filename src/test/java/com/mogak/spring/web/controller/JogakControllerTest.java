package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.exception.JogakException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.command.CreateJogakCommand;
import com.mogak.spring.service.command.UpdateJogakCommand;
import com.mogak.spring.service.result.CreateJogakResult;
import com.mogak.spring.service.result.DailyJogakListResult;
import com.mogak.spring.service.result.DailyJogakResult;
import com.mogak.spring.service.result.JogakDailyResult;
import com.mogak.spring.service.result.JogakDetailResult;
import com.mogak.spring.service.result.OneTimeJogakListResult;
import com.mogak.spring.service.result.OneTimeJogakResult;
import com.mogak.spring.service.result.RoutineJogakResult;
import com.mogak.spring.web.dto.jogakdto.UpdateJogakRequest;
import com.mogak.spring.support.SecurityContextTestHelper;

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
@WebMvcTest(JogakController.class)
@ActiveProfiles("test")
class JogakControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private JogakService jogakService;
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
    @DisplayName("조각 생성 요청이 성공하면 생성 응답 계약을 반환한다")
    void createJogakContract() throws Exception {
        when(jogakService.createJogak(eq(1L), any(CreateJogakCommand.class))).thenReturn(
                new CreateJogakResult(
                        1L,
                        "정보처리기사",
                        "자격증",
                        "문제풀이",
                        false,
                        null,
                        0,
                        LocalDate.of(2026, 3, 26),
                        null
                )
        );

        mockMvc.perform(post("/api/jogaks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mogakId\":1,\"title\":\"문제풀이\",\"isRoutine\":false,\"today\":\"2026-03-26\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.jogakId").value(1L))
                .andExpect(jsonPath("$.result.title").value("문제풀이"));
    }

    @Test
    @DisplayName("조각 생성 요청이 유효하지 않으면 에러 응답 계약을 반환한다")
    void createJogakValidationErrorContract() throws Exception {
        mockMvc.perform(post("/api/jogaks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mogakId\":1,\"title\":\"문제풀이\",\"isRoutine\":false}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("Z005"))
                .andExpect(jsonPath("$.message").value("입력값이 유효하지 않습니다"));
    }

    @Test
    @DisplayName("일회성 조각 조회 요청이 성공하면 조회 응답 계약을 반환한다")
    void getDailyJogaksContract() throws Exception {
        when(jogakService.getDailyJogaks(anyLong(), eq(LocalDate.of(2026, 3, 26)))).thenReturn(
                new OneTimeJogakListResult(
                        1,
                        List.of(new OneTimeJogakResult(
                                1L,
                                "정보처리기사",
                                "자격증",
                                "문제풀이",
                                false,
                                true,
                                null,
                                null,
                                null
                        ))
                )
        );

        mockMvc.perform(get("/api/jogaks/daily")
                        .param("date", "2026-03-26"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.size").value(1))
                .andExpect(jsonPath("$.result.jogaks[0].title").value("문제풀이"));
    }

    @Test
    @DisplayName("루틴 조각 조회 요청이 성공하면 조회 응답 계약을 반환한다")
    void getRoutineJogaksContract() throws Exception {
        when(jogakService.getRoutineJogaks(anyLong(), eq(LocalDate.of(2026, 3, 26)), eq(LocalDate.of(2026, 3, 30)))).thenReturn(List.of(
                new RoutineJogakResult(-1L, LocalDate.of(2026, 3, 27), false, "루틴 조각")
        ));

        mockMvc.perform(get("/api/jogaks/routines")
                        .param("startDay", "2026-03-26")
                        .param("endDay", "2026-03-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].title").value("루틴 조각"))
                .andExpect(jsonPath("$.result[0].dailyJogakId").value(-1L));
    }

    @Test
    @DisplayName("일별 데일리 조각 조회 요청이 성공하면 query parameter 날짜로 조회 응답 계약을 반환한다")
    void getDayJogaksContract() throws Exception {
        when(jogakService.getDayJogaks(anyLong(), eq(LocalDate.of(2026, 3, 26)))).thenReturn(
                new DailyJogakListResult(
                        1,
                        List.of(new DailyJogakResult(
                                1L,
                                10L,
                                "정보처리기사",
                                "자격증",
                                "루틴 조각",
                                true,
                                false
                        ))
                )
        );

        mockMvc.perform(get("/api/jogaks")
                        .param("date", "2026-03-26"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.size").value(1))
                .andExpect(jsonPath("$.result.dailyJogaks[0].title").value("루틴 조각"));
    }

    @Test
    @DisplayName("조각 단일 조회 요청이 성공하면 조회 응답 계약을 반환한다")
    void getJogakContract() throws Exception {
        when(jogakService.getJogakDetail(1L, 1L)).thenReturn(
                new JogakDetailResult(
                        1L,
                        "정보처리기사",
                        "자격증",
                        "문제풀이",
                        false,
                        List.of("MONDAY"),
                        "#112233",
                        2,
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 3, 31)
                )
        );

        mockMvc.perform(get("/api/jogaks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.jogakId").value(1L))
                .andExpect(jsonPath("$.result.title").value("문제풀이"));
    }

    @Test
    @DisplayName("조각 시작 요청이 성공하면 성공 응답 계약을 반환한다")
    void startJogakContract() throws Exception {
        when(jogakService.startJogak(1L, 1L)).thenReturn(
                new JogakDailyResult(
                        1L,
                        10L,
                        "문제풀이",
                        "정보처리기사",
                        "자격증",
                        false,
                        null,
                        false,
                        0
                )
        );

        mockMvc.perform(post("/api/jogaks/1/start"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.dailyJogakId").value(10L))
                .andExpect(jsonPath("$.result.isAchievement").value(false));
    }

    @Test
    @DisplayName("이미 시작한 조각의 시작을 요청하면 에러 응답 계약을 반환한다")
    void startJogakAlreadyStartedErrorContract() throws Exception {
        when(jogakService.startJogak(1L, 1L)).thenThrow(new JogakException(ErrorCode.ALREADY_START_JOGAK));

        mockMvc.perform(post("/api/jogaks/1/start"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value("J002"))
                .andExpect(jsonPath("$.message").value("이미 시작한 조각입니다"));
    }

    @Test
    @DisplayName("조각 수정 요청이 성공하면 수정 응답 계약을 반환한다")
    void updateJogakContract() throws Exception {
        when(jogakService.updateJogak(eq(1L), eq(1L), any(UpdateJogakCommand.class))).thenReturn(
                new CreateJogakResult(
                        1L,
                        "정보처리기사",
                        "자격증",
                        "문제풀이(수정)",
                        false,
                        List.of("MONDAY"),
                        0,
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 4, 30)
                )
        );

        mockMvc.perform(put("/api/jogaks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateJogakRequest(
                                "문제풀이(수정)",
                                false,
                                List.of("MONDAY"),
                                LocalDate.of(2026, 4, 30)
                        ))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.title").value("문제풀이(수정)"));
    }

    @Test
    @DisplayName("조각 삭제 요청이 성공하면 성공 응답 계약을 반환한다")
    void deleteJogakContract() throws Exception {
        mockMvc.perform(delete("/api/jogaks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    @DisplayName("조각 성공 요청이 성공하면 성공 응답 계약을 반환한다")
    void successJogakContract() throws Exception {
        when(jogakService.successJogak(1L, 10L)).thenReturn(
                new JogakDailyResult(
                        1L,
                        10L,
                        "문제풀이",
                        "정보처리기사",
                        "자격증",
                        false,
                        null,
                        true,
                        1
                )
        );

        mockMvc.perform(put("/api/daily-jogaks/10/success"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.isAchievement").value(true))
                .andExpect(jsonPath("$.result.achievements").value(1));
    }

    @Test
    @DisplayName("조각 실패 요청이 성공하면 응답 계약을 반환한다")
    void failJogakContract() throws Exception {
        when(jogakService.failJogak(1L, 10L)).thenReturn(
                new JogakDailyResult(
                        1L,
                        10L,
                        "문제풀이",
                        "정보처리기사",
                        "자격증",
                        false,
                        null,
                        false,
                        0
                )
        );

        mockMvc.perform(put("/api/daily-jogaks/10/fail"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.dailyJogakId").value(10L))
                .andExpect(jsonPath("$.result.isAchievement").value(false));
    }

    @Test
    @DisplayName("이미 종료한 조각의 성공을 요청하면 에러 응답 계약을 반환한다")
    void successJogakAlreadyEndedErrorContract() throws Exception {
        when(jogakService.successJogak(1L, 10L)).thenThrow(new JogakException(ErrorCode.ALREADY_END_JOGAK));

        mockMvc.perform(put("/api/daily-jogaks/10/success"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value("J003"))
                .andExpect(jsonPath("$.message").value("이미 종료한 조각입니다"));
    }

    @Test
    @DisplayName("존재하지 않는 조각의 성공을 요청하면 에러 응답 계약을 반환한다")
    void successJogakNotFoundErrorContract() throws Exception {
        when(jogakService.successJogak(1L, 999L)).thenThrow(new JogakException(ErrorCode.NOT_EXIST_JOGAK));

        mockMvc.perform(put("/api/daily-jogaks/999/success"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("J005"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 조각입니다"));
    }

    @Test
    @DisplayName("구버전 조각 생성 라우트는 더 이상 노출되지 않는다")
    void legacyCreateJogakRouteIsNotExposed() throws Exception {
        mockMvc.perform(post("/api/modarats/mogaks/jogaks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }
}
