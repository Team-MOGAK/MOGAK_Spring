package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.service.PostService;
import com.mogak.spring.service.StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private PostService postService;
    @MockitoBean
    private StorageService storageService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("이미지 포함 게시글 생성 요청은 storage 비활성 상태에서 503 에러 응답 계약을 반환한다")
    void createPostStorageDisabledContract() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(new com.mogak.spring.web.dto.postdto.PostRequestDto.CreatePostDto())
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        doThrow(new com.mogak.spring.exception.BaseException(ErrorCode.STORAGE_DISABLED))
                .when(storageService).uploadImg(any(), any());

        mockMvc.perform(multipart("/api/mogaks/{mogakId}/posts", 1L)
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
    @DisplayName("게시글 삭제 요청은 storage 비활성 상태에서 503 에러 응답 계약을 반환한다")
    void deletePostStorageDisabledContract() throws Exception {
        when(postService.findById(1L)).thenReturn(Post.builder()
                .id(1L)
                .contents("content")
                .postThumbnailUrl("thumbnail")
                .validation("VALID")
                .viewCnt(0)
                .build());
        when(postService.findAllImgByPost(any())).thenReturn(java.util.List.of());
        doThrow(new com.mogak.spring.exception.BaseException(ErrorCode.STORAGE_DISABLED))
                .when(storageService).deleteImg(any(), any());

        mockMvc.perform(delete("/api/mogaks/posts/{postId}", 1L))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.status").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.code").value("Z006"))
                .andExpect(jsonPath("$.message").value("스토리지 기능이 비활성화되어 있습니다"));
    }
}
