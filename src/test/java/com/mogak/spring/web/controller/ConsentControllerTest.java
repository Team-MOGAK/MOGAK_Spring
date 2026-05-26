package com.mogak.spring.web.controller;

import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.service.ConsentService;
import com.mogak.spring.service.command.MarketingConsentCommand;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ConsentItemResult;
import com.mogak.spring.service.result.MarketingConsentResult;
import com.mogak.spring.support.SecurityContextTestHelper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ConsentController.class)
@ActiveProfiles("test")
class ConsentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsentService consentService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("동의 항목 조회는 BaseResponse 배열 계약을 반환한다")
    void getConsentsReturnsBaseResponseContract() throws Exception {
        when(consentService.getActiveConsentItems()).thenReturn(List.of(
                new ConsentItemResult(1L, "TERMS", "이용약관", "서비스 이용약관", true),
                new ConsentItemResult(2L, "MARKETING", "마케팅 수신", "이벤트 알림 수신", false)
        ));

        mockMvc.perform(get("/api/consents"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].id").value(1L))
                .andExpect(jsonPath("$.result[0].code").value("TERMS"))
                .andExpect(jsonPath("$.result[0].name").value("이용약관"))
                .andExpect(jsonPath("$.result[0].description").value("서비스 이용약관"))
                .andExpect(jsonPath("$.result[0].required").value(true))
                .andExpect(jsonPath("$.result[1].id").value(2L))
                .andExpect(jsonPath("$.result[1].code").value("MARKETING"))
                .andExpect(jsonPath("$.result[1].required").value(false));
    }

    @Test
    @DisplayName("현재 사용자의 광고와 마케팅 동의 상태를 조회한다")
    void getMarketingConsentContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(10L, "user@test.com", SecurityAuthority.USER.getAuthority());
        when(consentService.getMarketingConsent(10L))
                .thenReturn(new MarketingConsentResult(true, false));

        mockMvc.perform(get("/api/users/marketing-consent"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result.marketingAgreed").value(true))
                .andExpect(jsonPath("$.result.advertisementAgreed").value(false));
    }

    @Test
    @DisplayName("사용자 동의 변경 요청은 인증 유저와 동의 목록을 서비스로 전달한다")
    void updateUserConsentsForwardsAuthenticatedUserAndAgreements() throws Exception {
        SecurityContextTestHelper.setAuthentication(10L, "user@test.com", SecurityAuthority.USER.getAuthority());

        mockMvc.perform(put("/api/users/consents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"consents":[{"consentItemId":1,"agreed":true},{"consentItemId":2,"agreed":false}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"));

        ArgumentCaptor<List<UserConsentCommand>> captor = ArgumentCaptor.captor();
        verify(consentService).updateUserConsents(org.mockito.Mockito.eq(10L), captor.capture());
        assertThat(captor.getValue()).containsExactly(
                new UserConsentCommand(1L, true),
                new UserConsentCommand(2L, false)
        );
    }

    @Test
    @DisplayName("현재 사용자의 광고와 마케팅 동의 상태 중 요청된 값만 변경한다")
    void patchMarketingConsentContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(10L, "user@test.com", SecurityAuthority.USER.getAuthority());
        when(consentService.updateMarketingConsent(anyLong(), any(MarketingConsentCommand.class)))
                .thenReturn(new MarketingConsentResult(true, false));

        mockMvc.perform(patch("/api/users/marketing-consent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marketingAgreed\":true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.marketingAgreed").value(true))
                .andExpect(jsonPath("$.result.advertisementAgreed").value(false));

        ArgumentCaptor<MarketingConsentCommand> captor = ArgumentCaptor.forClass(MarketingConsentCommand.class);
        verify(consentService).updateMarketingConsent(org.mockito.Mockito.eq(10L), captor.capture());
        assertThat(captor.getValue().marketingAgreed()).isTrue();
        assertThat(captor.getValue().advertisementAgreed()).isNull();
    }

    @Test
    @DisplayName("사용자 동의 변경 요청에 null 항목이 있으면 400을 반환한다")
    void updateUserConsentsRejectsNullAgreementItem() throws Exception {
        SecurityContextTestHelper.setAuthentication(10L, "user@test.com", SecurityAuthority.USER.getAuthority());

        mockMvc.perform(put("/api/users/consents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"consents":[null]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(consentService, never()).updateUserConsents(anyLong(), anyList());
    }

    @Test
    @DisplayName("변경할 동의 값이 없는 요청은 400을 반환한다")
    void patchMarketingConsentRejectsEmptyRequest() throws Exception {
        SecurityContextTestHelper.setAuthentication(10L, "user@test.com", SecurityAuthority.USER.getAuthority());

        mockMvc.perform(patch("/api/users/marketing-consent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(consentService, never()).updateMarketingConsent(anyLong(), any());
    }
}
