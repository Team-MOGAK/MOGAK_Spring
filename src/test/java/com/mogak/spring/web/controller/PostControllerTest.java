package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.GlobalExceptionHandler;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.service.PostService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

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
        PostRequestDto.CreatePostDto request = createRequest("content");
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
        List<PostImgRequestDto.CreatePostImgDto> uploadedImages = List.of(
                PostImgRequestDto.CreatePostImgDto.builder()
                        .imgName("post.png")
                        .imgUrl("https://example.com/post.png")
                        .thumbnail(true)
                        .build()
        );
        Post post = createPost(1L, 7L, 1L, "content", uploadedImages.get(0).getImgUrl());

        doNothing().when(postService).validateCreateAccess(eq(7L), any(), anyList(), eq(1L));
        when(storageService.uploadImg(any(), any())).thenReturn(uploadedImages);
        when(postService.create(eq(7L), any(), eq(uploadedImages), eq(1L))).thenReturn(post);

                mockMvc.perform(multipart("/api/mogaks/{mogakId}/posts", 1L)
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
        inOrder.verify(postService).validateCreateAccess(eq(7L), any(), anyList(), eq(1L));
        inOrder.verify(storageService).uploadImg(any(), any());
        inOrder.verify(postService).create(eq(7L), any(), eq(uploadedImages), eq(1L));
    }

    @Test
    @DisplayName("게시글 생성 preflight가 실패하면 storage 업로드는 호출되지 않는다")
    void createPostPreflightFailureDoesNotUpload() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        PostRequestDto.CreatePostDto request = createRequest("content");
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
                .validateCreateAccess(eq(7L), any(), anyList(), eq(1L));

        mockMvc.perform(multipart("/api/mogaks/{mogakId}/posts", 1L)
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
        verify(postService, never()).create(any(), any(), anyList(), any());
    }

    @Test
    @DisplayName("모각별 게시글 조회는 인증 사용자의 id를 서비스에 전달한다")
    void getPostListUsesAuthenticatedUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        Slice<Post> posts = new SliceImpl<>(List.of());
        when(postService.getAllPosts(7L, 0, 1L, 10)).thenReturn(posts);

        mockMvc.perform(get("/api/mogaks/{mogakId}/posts", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(postService).getAllPosts(7L, 0, 1L, 10);
    }

    @Test
    @DisplayName("게시글 상세 조회는 인증 사용자의 id를 서비스에 전달한다")
    void getPostDetailUsesAuthenticatedUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        Post post = createPost(1L, 7L, 1L, "content", "https://example.com/post.png");
        when(postService.findById(7L, 1L)).thenReturn(post);
        when(postService.findNotThumbnailImg(post)).thenReturn(List.of("https://example.com/post-2.png"));

        mockMvc.perform(get("/api/mogaks/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postId").value(1));

        verify(postService).findById(7L, 1L);
    }

    @Test
    @DisplayName("게시글 수정은 인증 사용자의 id를 서비스에 전달한다")
    void updatePostUsesAuthenticatedUserId() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        PostRequestDto.UpdatePostDto request = updateRequest("updated");
        Post post = createPost(1L, 7L, 1L, "updated", "https://example.com/post.png");
        when(postService.update(eq(7L), eq(1L), any())).thenReturn(post);

        mockMvc.perform(put("/api/mogaks/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1));

        verify(postService).update(eq(7L), eq(1L), any());
    }

    @Test
    @DisplayName("게시글 삭제는 owner 검증 후 storage 삭제와 db 삭제를 순서대로 호출한다")
    void deletePostValidatesBeforeStorageDeleteAndDelete() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        Post post = createPost(1L, 7L, 1L, "content", "https://example.com/post.png");
        PostImg postImg = PostImg.builder()
                .id(11L)
                .post(post)
                .imgName("post.png")
                .imgUrl("https://example.com/post.png")
                .build();

        when(postService.findById(7L, 1L)).thenReturn(post);
        when(postService.findAllImgByPost(post)).thenReturn(List.of(postImg));
        doNothing().when(storageService).deleteImg(any(), any());
        doNothing().when(postService).delete(7L, 1L);

        mockMvc.perform(delete("/api/mogaks/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.validation").value("INACTIVE"));

        InOrder inOrder = inOrder(postService, storageService);
        inOrder.verify(postService).findById(7L, 1L);
        inOrder.verify(postService).findAllImgByPost(post);
        inOrder.verify(storageService).deleteImg(any(), any());
        inOrder.verify(postService).delete(7L, 1L);
    }

    @Test
    @DisplayName("게시글 삭제 owner 검증이 실패하면 storage 삭제는 호출되지 않는다")
    void deletePostOwnerFailureDoesNotCallStorage() throws Exception {
        SecurityContextTestHelper.setAuthentication(7L, "writer@test.com", "ROLE_USER");
        doThrow(new AuthException(ErrorCode.INVALID_PERMISSION))
                .when(postService)
                .findById(7L, 1L);

        mockMvc.perform(delete("/api/mogaks/posts/{postId}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("T004"));

        verify(storageService, never()).deleteImg(any(), any());
        verify(postService, never()).delete(any(), any());
    }

    private PostRequestDto.CreatePostDto createRequest(String contents) {
        PostRequestDto.CreatePostDto request = new PostRequestDto.CreatePostDto();
        ReflectionTestUtils.setField(request, "contents", contents);
        return request;
    }

    private PostRequestDto.UpdatePostDto updateRequest(String contents) {
        PostRequestDto.UpdatePostDto request = new PostRequestDto.UpdatePostDto();
        ReflectionTestUtils.setField(request, "contents", contents);
        return request;
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
        Post post = Post.builder()
                .id(postId)
                .mogak(mogak)
                .user(writer)
                .contents(contents)
                .postThumbnailUrl(thumbnailUrl)
                .validation("ACTIVE")
                .viewCnt(0)
                .build();
        return post;
    }
}
