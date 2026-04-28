package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import com.mogak.spring.service.ModaratService;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
@WebMvcTest(ModaratController.class)
@ActiveProfiles("test")
class ModaratControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ModaratService modaratService;
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
    @DisplayName("모다라트 생성 요청이 성공하면 생성 응답 계약을 반환한다")
    void createModaratContract() throws Exception {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", TestFixtureFactory.job("개발"), TestFixtureFactory.address("서울"));
        Modarat modarat = TestFixtureFactory.modarat(10L, user, "메인 모다라트", "#112233");
        when(modaratService.create(anyLong(), any(ModaratRequestDto.CreateModaratDto.class))).thenReturn(modarat);
        ModaratRequestDto.CreateModaratDto request = new ModaratRequestDto.CreateModaratDto("메인 모다라트", "#112233");

        mockMvc.perform(post("/api/modarats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.id").value(10L))
                .andExpect(jsonPath("$.result.title").value("메인 모다라트"));

        verify(modaratService).create(eq(1L), any(ModaratRequestDto.CreateModaratDto.class));
    }

    @Test
    @DisplayName("모다라트 상세 조회 요청이 성공하면 userId를 서비스로 전달한다")
    void getDetailModaratForwardsUserId() throws Exception {
        when(modaratService.getDetailModarat(eq(1L), eq(10L))).thenReturn(new SingleDetailModaratDto(10L, "메인 모다라트", "#112233"));

        mockMvc.perform(get("/api/modarats/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.id").value(10L))
                .andExpect(jsonPath("$.result.title").value("메인 모다라트"));

        verify(modaratService).getDetailModarat(1L, 10L);
    }

    @Test
    @DisplayName("모다라트 수정 요청이 성공하면 userId를 서비스로 전달한다")
    void updateModaratForwardsUserId() throws Exception {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", TestFixtureFactory.job("개발"), TestFixtureFactory.address("서울"));
        Modarat modarat = TestFixtureFactory.modarat(10L, user, "수정된 모다라트", "#445566");
        when(modaratService.update(eq(1L), eq(10L), any(ModaratRequestDto.UpdateModaratDto.class))).thenReturn(modarat);
        ModaratRequestDto.UpdateModaratDto request = new ModaratRequestDto.UpdateModaratDto("수정된 모다라트", "#445566");

        mockMvc.perform(put("/api/modarats/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(10L))
                .andExpect(jsonPath("$.result.title").value("수정된 모다라트"));

        verify(modaratService).update(eq(1L), eq(10L), any(ModaratRequestDto.UpdateModaratDto.class));
    }

    @Test
    @DisplayName("모다라트 삭제 요청이 성공하면 userId를 서비스로 전달한다")
    void deleteModaratForwardsUserId() throws Exception {
        mockMvc.perform(delete("/api/modarats/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(modaratService).delete(1L, 10L);
    }
}
