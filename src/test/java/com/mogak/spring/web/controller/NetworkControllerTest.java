package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.service.PostLikeService;
import com.mogak.spring.service.PostService;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.web.dto.postdto.PostLikeRequestDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NetworkController.class)
@ActiveProfiles("test")
class NetworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private PostLikeService postLikeService;
    @MockitoBean
    private PostService postService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("좋아요 요청은 기존 POST /api/posts/like 바디 계약으로 토글 서비스에 위임한다")
    void updateLikeContractForwardsBodyAndUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "user@test.com", "ROLE_USER");
        PostLikeRequestDto.LikeDto request = likeRequest(10L);
        when(postLikeService.updateLike(eq(7L), any(PostLikeRequestDto.LikeDto.class)))
                .thenReturn("좋아요가 생성되었습니다");

        mockMvc.perform(post("/api/posts/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("좋아요가 생성되었습니다"));

        ArgumentCaptor<PostLikeRequestDto.LikeDto> requestCaptor = ArgumentCaptor.forClass(PostLikeRequestDto.LikeDto.class);
        verify(postLikeService).updateLike(eq(7L), requestCaptor.capture());
        assertThat(requestCaptor.getValue().postId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("좋아요 토글 서비스 예외는 전역 에러 응답으로 변환된다")
    void updateLikeMapsServiceError() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "user@test.com", "ROLE_USER");
        PostLikeRequestDto.LikeDto request = likeRequest(10L);
        when(postLikeService.updateLike(eq(7L), any(PostLikeRequestDto.LikeDto.class)))
                .thenThrow(new PostException(ErrorCode.NOT_EXIST_POST));

        mockMvc.perform(post("/api/posts/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("P003"));
    }

    @Test
    @DisplayName("좋아요 요청 postId가 없으면 서비스 호출 전에 입력값 오류를 반환한다")
    void updateLikeRejectsMissingPostIdBeforeServiceCall() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "user@test.com", "ROLE_USER");

        mockMvc.perform(post("/api/posts/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(postLikeService, never()).updateLike(any(), any(PostLikeRequestDto.LikeDto.class));
    }

    @Test
    @DisplayName("분리된 좋아요 생성/삭제 라우트는 제공하지 않는다")
    void splitLikeRoutesAreNotExposed() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "user@test.com", "ROLE_USER");

        mockMvc.perform(post("/api/posts/{postId}/like", 10L))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/posts/{postId}/like", 10L))
                .andExpect(status().isNotFound());
    }

    private PostLikeRequestDto.LikeDto likeRequest(Long postId) {
        return new PostLikeRequestDto.LikeDto(postId);
    }
}
