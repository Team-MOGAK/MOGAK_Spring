package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.service.PostService;
import com.mogak.spring.service.StorageCleanupService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.result.PostSummaryResult;
import com.mogak.spring.service.result.UploadedPostImageResult;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.postdto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private StorageCleanupService storageCleanupService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("게시글 생성은 preflight 후 storage 업로드, 생성 순서로 처리된다")
    void createPostValidatesBeforeUploadAndCreate() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest("content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );
        List<UploadedPostImageResult> uploadedImages = List.of(
                new UploadedPostImageResult(
                        "post.png",
                        "https://example.com/post.png",
                        true
                )
        );
        Post post = createPost(1L, 7L, 1L, "content", uploadedImages.get(0).imgUrl());

        doNothing().when(postService).validateCreateAccess(eq(7L), any(LocalDate.class), any(String.class), anyList(), eq(1L));
        when(storageService.uploadImg(any(), any())).thenReturn(uploadedImages);
        when(postService.create(eq(7L), any(LocalDate.class), any(String.class), eq(uploadedImages), eq(1L))).thenReturn(post);

                mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(image)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.userId").value(7));

        InOrder inOrder = inOrder(postService, storageService);
        inOrder.verify(postService).validateCreateAccess(eq(7L), any(LocalDate.class), any(String.class), anyList(), eq(1L));
        inOrder.verify(storageService).uploadImg(any(), any());
        inOrder.verify(postService).create(eq(7L), any(LocalDate.class), any(String.class), eq(uploadedImages), eq(1L));
    }

    @Test
    @DisplayName("게시글 생성은 이미지 part가 없어도 storage 업로드 없이 생성한다")
    void createPostWithoutMultipartFileCreatesWithoutStorageUpload() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest("content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        Post post = createPost(1L, 7L, 1L, "content", null);

        when(postService.create(eq(7L), any(LocalDate.class), eq("content"), eq(List.of()), eq(1L))).thenReturn(post);

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.imgUrls").isArray())
                .andExpect(jsonPath("$.result.imgUrls").isEmpty());

        verify(postService).validateCreateAccess(eq(7L), any(LocalDate.class), eq("content"), eq(List.of()), eq(1L));
        verify(storageService, never()).uploadImg(any(), any());
        verify(postService).create(eq(7L), any(LocalDate.class), eq("content"), eq(List.of()), eq(1L));
    }

    @Test
    @DisplayName("게시글 생성은 빈 이미지 part를 이미지 없음으로 처리한다")
    void createPostWithEmptyMultipartFileTreatsImageAsMissing() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest("content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile emptyImage = new MockMultipartFile(
                "multipartFile",
                "",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[0]
        );
        Post post = createPost(1L, 7L, 1L, "content", null);

        when(postService.create(eq(7L), any(LocalDate.class), eq("content"), eq(List.of()), eq(1L))).thenReturn(post);

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(emptyImage)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.imgUrls").isArray())
                .andExpect(jsonPath("$.result.imgUrls").isEmpty());

        verify(postService).validateCreateAccess(eq(7L), any(LocalDate.class), eq("content"), eq(List.of()), eq(1L));
        verify(storageService, never()).uploadImg(any(), any());
        verify(postService).create(eq(7L), any(LocalDate.class), eq("content"), eq(List.of()), eq(1L));
    }

    @Test
    @DisplayName("게시글 생성은 실제 이미지 업로드 중 storage 비활성화를 503으로 전파한다")
    void createPostWithActualImagePropagatesStorageDisabled() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest("content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        when(storageService.uploadImg(any(), eq("img"))).thenThrow(new BaseException(ErrorCode.STORAGE_DISABLED));

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(image)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("Z006"));

        verify(storageService).uploadImg(any(), eq("img"));
        verify(postService, never()).create(any(), any(), any(), anyList(), any());
        verify(storageCleanupService, never()).deleteUploadedImagesBestEffort(anyList(), any());
    }

    @Test
    @DisplayName("게시글 생성 preflight가 실패하면 storage 업로드는 호출되지 않는다")
    void createPostPreflightFailureDoesNotUpload() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest("content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        doThrow(new AuthException(ErrorCode.INVALID_PERMISSION))
                .when(postService)
                .validateCreateAccess(eq(7L), any(LocalDate.class), any(String.class), anyList(), eq(1L));

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(image)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("T004"));

        verify(storageService, never()).uploadImg(any(), any());
        verify(postService, never()).create(any(), any(), any(), anyList(), any());
    }

    @Test
    @DisplayName("게시글 생성 중 DB 생성이 실패하면 업로드된 이미지를 보상 삭제한다")
    void createPostCleansUpUploadedImagesWhenCreateFails() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest("content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );
        List<UploadedPostImageResult> uploadedImages = List.of(
                new UploadedPostImageResult(
                        "post.png",
                        "https://example.com/post.png",
                        true
                )
        );

        doNothing().when(postService).validateCreateAccess(eq(7L), any(LocalDate.class), any(String.class), anyList(), eq(1L));
        when(storageService.uploadImg(any(), any())).thenReturn(uploadedImages);
        when(postService.create(eq(7L), any(LocalDate.class), any(String.class), eq(uploadedImages), eq(1L)))
                .thenThrow(new PostException(ErrorCode.ALREADY_EXISTS_POST));

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(image)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("P005"));

        verify(storageCleanupService).deleteUploadedImagesBestEffort(uploadedImages, "img");
    }

    @Test
    @DisplayName("게시글 생성은 targetDate가 없으면 서비스 호출 전에 입력값 오류를 반환한다")
    void createPostRejectsMissingTargetDateBeforeServiceCall() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = new CreatePostRequest(null, "content");
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(image)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(postService, never()).validateCreateAccess(any(), any(), any(), anyList(), any());
        verify(storageService, never()).uploadImg(any(), any());
    }

    @Test
    @DisplayName("게시글 생성은 contents가 없으면 서비스 호출 전에 입력값 오류를 반환한다")
    void createPostRejectsMissingContentsBeforeServiceCall() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        CreatePostRequest request = createRequest(null);
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile image = new MockMultipartFile(
                "multipartFile",
                "post.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes()
        );

        mockMvc.perform(multipart("/api/jogaks/{jogakId}/posts", 1L)
                        .file(requestPart)
                        .file(image)
                        .with(mockRequest -> {
                            mockRequest.setMethod("POST");
                            return mockRequest;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(postService, never()).validateCreateAccess(any(), any(), any(), anyList(), any());
        verify(storageService, never()).uploadImg(any(), any());
    }

    @Test
    @DisplayName("모각별 게시글 조회는 인증 사용자의 id를 서비스에 전달한다")
    void getPostListUsesAuthenticatedUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        Slice<PostSummaryResult> posts = new SliceImpl<>(List.of());
        when(postService.getAllPosts(7L, 0, 1L, 10)).thenReturn(posts);

        mockMvc.perform(get("/api/mogaks/{mogakId}/posts", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(postService).getAllPosts(7L, 0, 1L, 10);
    }

    @Test
    @DisplayName("모각별 게시글 조회는 기존 Slice JSON 계약을 유지한다")
    void getPostListReturnsSliceResponseContract() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        PostSummaryResult post = new PostSummaryResult(
                11L,
                1L,
                2L,
                3L,
                LocalDate.of(2026, 4, 21),
                "오늘 회고",
                "https://example.com/thumb.png",
                4
        );
        Slice<PostSummaryResult> posts = new SliceImpl<>(List.of(post), PageRequest.of(0, 10), true);
        when(postService.getAllPosts(7L, 0, 1L, 10)).thenReturn(posts);

        mockMvc.perform(get("/api/mogaks/{mogakId}/posts", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.result.content[0].postId").value(11))
                .andExpect(jsonPath("$.result.content[0].mogakId").value(1))
                .andExpect(jsonPath("$.result.content[0].jogakId").value(2))
                .andExpect(jsonPath("$.result.content[0].dailyJogakId").value(3))
                .andExpect(jsonPath("$.result.content[0].targetDate").value("2026-04-21"))
                .andExpect(jsonPath("$.result.content[0].contents").value("오늘 회고"))
                .andExpect(jsonPath("$.result.content[0].thumbnailUrl").value("https://example.com/thumb.png"))
                .andExpect(jsonPath("$.result.content[0].likeCnt").value(4))
                .andExpect(jsonPath("$.result.size").value(10))
                .andExpect(jsonPath("$.result.number").value(0))
                .andExpect(jsonPath("$.result.numberOfElements").value(1))
                .andExpect(jsonPath("$.result.first").value(true))
                .andExpect(jsonPath("$.result.last").value(false));
    }

    @Test
    @DisplayName("모각별 게시글 조회는 이미지 없는 게시글의 thumbnailUrl을 null로 반환한다")
    void getPostListReturnsNullThumbnailUrlWhenPostHasNoImage() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        PostSummaryResult post = new PostSummaryResult(
                11L,
                1L,
                2L,
                3L,
                LocalDate.of(2026, 4, 21),
                "오늘 회고",
                null,
                4
        );
        Slice<PostSummaryResult> posts = new SliceImpl<>(List.of(post), PageRequest.of(0, 10), false);
        when(postService.getAllPosts(7L, 0, 1L, 10)).thenReturn(posts);

        mockMvc.perform(get("/api/mogaks/{mogakId}/posts", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].thumbnailUrl").isEmpty());
    }

    @Test
    @DisplayName("조각 날짜별 게시글 단건 조회는 복수 posts 경로와 targetDate를 사용한다")
    void getPostByJogakAndDateUsesPluralPostsPathAndTargetDate() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        LocalDate targetDate = LocalDate.of(2026, 4, 21);
        Post post = createPost(1L, 7L, 1L, "content", "https://example.com/post.png");
        when(postService.getByJogakAndTargetDate(7L, 20L, targetDate)).thenReturn(post);
        when(postService.findNotThumbnailImg(post)).thenReturn(List.of("https://example.com/post-2.png"));
        when(postService.findActiveCommentIds(post)).thenReturn(List.of(10L));

        mockMvc.perform(get("/api/jogaks/{jogakId}/posts", 20L)
                        .param("targetDate", targetDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.commentId[0]").value(10));

        verify(postService).getByJogakAndTargetDate(7L, 20L, targetDate);
        verify(postService).findActiveCommentIds(post);
    }

    @Test
    @DisplayName("조각 날짜별 게시글 단건 조회는 단수 post 경로를 노출하지 않는다")
    void getPostByJogakAndDateDoesNotExposeSingularPostPath() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");

        mockMvc.perform(get("/api/jogaks/{jogakId}/post", 20L)
                        .param("targetDate", "2026-04-21"))
                .andExpect(status().isNotFound());

        verify(postService, never()).getByJogakAndTargetDate(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 상세 조회는 인증 사용자의 id를 서비스에 전달한다")
    void getPostDetailUsesAuthenticatedUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        Post post = createPost(1L, 7L, 1L, "content", "https://example.com/post.png");
        when(postService.findById(7L, 1L)).thenReturn(post);
        when(postService.findNotThumbnailImg(post)).thenReturn(List.of("https://example.com/post-2.png"));
        when(postService.findActiveCommentIds(post)).thenReturn(List.of(10L));

        mockMvc.perform(get("/api/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postId").value(1))
                .andExpect(jsonPath("$.result.commentId[0]").value(10));

        verify(postService).findById(7L, 1L);
        verify(postService).findActiveCommentIds(post);
    }

    @Test
    @DisplayName("게시글 수정은 인증 사용자의 id를 서비스에 전달한다")
    void updatePostUsesAuthenticatedUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        UpdatePostRequest request = updateRequest("updated");
        Post post = createPost(1L, 7L, 1L, "updated", "https://example.com/post.png");
        when(postService.update(eq(7L), eq(1L), any())).thenReturn(post);

        mockMvc.perform(put("/api/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1));

        verify(postService).update(eq(7L), eq(1L), any());
    }

    @Test
    @DisplayName("게시글 수정은 contents가 없으면 서비스 호출 전에 입력값 오류를 반환한다")
    void updatePostRejectsMissingContentsBeforeServiceCall() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        UpdatePostRequest request = updateRequest(null);

        mockMvc.perform(put("/api/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Z005"));

        verify(postService, never()).update(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 삭제는 owner 검증 후 storage 삭제와 db 삭제를 순서대로 호출한다")
    void deletePostValidatesBeforeStorageDeleteAndDelete() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        doNothing().when(postService).delete(7L, 1L);

        mockMvc.perform(delete("/api/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.deleted").value(true));

        InOrder inOrder = inOrder(postService, storageService);
        inOrder.verify(postService).delete(7L, 1L);
    }

    @Test
    @DisplayName("게시글 삭제 owner 검증이 실패하면 storage 삭제는 호출되지 않는다")
    void deletePostOwnerFailureDoesNotCallStorage() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        doThrow(new AuthException(ErrorCode.INVALID_PERMISSION))
                .when(postService)
                .delete(7L, 1L);

        mockMvc.perform(delete("/api/posts/{postId}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("T004"));

        verify(postService).delete(7L, 1L);
        verify(storageService, never()).deleteImg(anyList(), any());
        verify(storageCleanupService, never()).deleteUploadedImagesBestEffort(anyList(), any());
    }

    private CreatePostRequest createRequest(String contents) {
        return new CreatePostRequest(java.time.LocalDate.now(), contents);
    }

    private UpdatePostRequest updateRequest(String contents) {
        return new UpdatePostRequest(contents);
    }

    private Post createPost(Long postId, Long userId, Long mogakId, String contents, String thumbnailUrl) {
        var writer = TestFixtureFactory.user(userId, "writer@test.com", "writer", null, null);
        Mogak mogak = TestFixtureFactory.mogak(
                mogakId,
                writer,
                TestFixtureFactory.modarat(1L, writer, "modarat", "#000000"),
                TestFixtureFactory.category(1, "자격증"),
                "mogak",
                "#112233"
        );
        var jogak = TestFixtureFactory.jogak(20L, mogak, "jogak", false, java.time.LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(30L, jogak, false);
        Post post = Post.builder()
                .id(postId)
                .dailyJogak(dailyJogak)
                .user(writer)
                .contents(contents)
                .postThumbnailUrl(thumbnailUrl)
                .viewCnt(0)
                .build();
        return post;
    }
}
