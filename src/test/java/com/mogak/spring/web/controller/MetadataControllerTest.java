package com.mogak.spring.web.controller;

import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.service.MetadataService;
import com.mogak.spring.service.result.metadata.MetadataOptionResult;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MetadataController.class)
@ActiveProfiles("test")
class MetadataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetadataService metadataService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("직업 메타데이터 조회는 BaseResponse 배열 계약을 반환한다")
    void getJobsReturnsBaseResponseContract() throws Exception {
        when(metadataService.getJobs()).thenReturn(List.of(
                new MetadataOptionResult("개발/데이터"),
                new MetadataOptionResult("기획/전략")
        ));

        mockMvc.perform(get("/api/metadata/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].name").value("개발/데이터"))
                .andExpect(jsonPath("$.result[1].name").value("기획/전략"));
    }

    @Test
    @DisplayName("주소 메타데이터 조회는 BaseResponse 배열 계약을 반환한다")
    void getAddressesReturnsBaseResponseContract() throws Exception {
        when(metadataService.getAddresses()).thenReturn(List.of(
                new MetadataOptionResult("서울특별시"),
                new MetadataOptionResult("경기도")
        ));

        mockMvc.perform(get("/api/metadata/addresses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].name").value("서울특별시"))
                .andExpect(jsonPath("$.result[1].name").value("경기도"));
    }

    @Test
    @DisplayName("모각 카테고리 메타데이터 조회는 BaseResponse 배열 계약을 반환한다")
    void getMogakCategoriesReturnsBaseResponseContract() throws Exception {
        when(metadataService.getMogakCategories()).thenReturn(List.of(
                new MetadataOptionResult("자격증"),
                new MetadataOptionResult("직무공부")
        ));

        mockMvc.perform(get("/api/metadata/mogak-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].name").value("자격증"))
                .andExpect(jsonPath("$.result[1].name").value("직무공부"));
    }

    @Test
    @DisplayName("색상 메타데이터 조회는 BaseResponse 배열 계약을 반환한다")
    void getColorsReturnsBaseResponseContract() throws Exception {
        when(metadataService.getColors()).thenReturn(List.of(
                new MetadataOptionResult("#475FFD"),
                new MetadataOptionResult("#FF4C77"),
                new MetadataOptionResult("#F98A08"),
                new MetadataOptionResult("#11D796"),
                new MetadataOptionResult("#FF6827"),
                new MetadataOptionResult("#9C31FF"),
                new MetadataOptionResult("#21CAFF"),
                new MetadataOptionResult("#FF2F2F")
        ));

        mockMvc.perform(get("/api/metadata/colors"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.result[0].name").value("#475FFD"))
                .andExpect(jsonPath("$.result[7].name").value("#FF2F2F"));
    }
}
