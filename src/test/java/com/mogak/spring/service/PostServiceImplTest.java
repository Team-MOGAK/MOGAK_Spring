package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.jogak.DailyJogakStatus;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private MogakRepository mogakRepository;
    @Mock
    private JogakRepository jogakRepository;
    @Mock
    private DailyJogakRepository dailyJogakRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostImgRepository postImgRepository;
    @Mock
    private PostCommentRepository postCommentRepository;
    @Mock
    private StorageCleanupService storageCleanupService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("게시글 생성 preflight는 다른 사용자의 모각이면 권한 예외를 반환한다")
    void validateCreateAccessThrowsInvalidPermissionWhenMogakBelongsToOtherUser() {
        User owner = user(1L, "owner@test.com");
        User other = user(2L, "other@test.com");
        var jogak = TestFixtureFactory.jogak(20L, mogak(10L, owner), "jogak", false, java.time.LocalDate.now(), null, 0);
        PostRequestDto.CreatePostDto request = createRequest("content");
        List<MultipartFile> images = List.of(image("post.png"));
        var dailyJogak = TestFixtureFactory.dailyJogak(10L, jogak, request.getTargetDate(), DailyJogakStatus.SUCCESS);

        when(dailyJogakRepository.findActiveByJogakIdAndTargetDateWithJogakGraph(10L, request.getTargetDate()))
                .thenReturn(Optional.of(dailyJogak));

        Throwable throwable = catchThrowable(() -> postService.validateCreateAccess(2L, request, images, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(postRepository, never()).existsByDailyJogakIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    @DisplayName("게시글 생성 preflight는 본문 길이가 초과되면 업로드 전에 예외를 반환한다")
    void validateCreateAccessThrowsExceedMaxNumPostWhenContentsTooLong() {
        User owner = user(1L, "owner@test.com");
        PostRequestDto.CreatePostDto request = createRequest("x".repeat(351));
        List<MultipartFile> images = List.of(image("post.png"));

        Throwable throwable = catchThrowable(() -> postService.validateCreateAccess(1L, request, images, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.EXCEED_MAX_NUM_POST);
        verify(postRepository, never()).existsByDailyJogakIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    @DisplayName("게시글 생성 preflight는 본문이 없으면 입력값 오류를 반환한다")
    void validateCreateAccessThrowsInvalidParameterWhenContentsMissing() {
        User owner = user(1L, "owner@test.com");
        PostRequestDto.CreatePostDto request = createRequest(null);
        List<MultipartFile> images = List.of(image("post.png"));

        Throwable throwable = catchThrowable(() -> postService.validateCreateAccess(1L, request, images, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
        verify(postRepository, never()).existsByDailyJogakIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    @DisplayName("게시글 생성 preflight는 같은 데일리 조각의 활성 게시글이 있으면 업로드 전에 예외를 반환한다")
    void validateCreateAccessThrowsAlreadyExistsPostBeforeUploadWhenActivePostExists() {
        User owner = user(1L, "owner@test.com");
        PostRequestDto.CreatePostDto request = createRequest("content");
        List<MultipartFile> images = List.of(image("post.png"));
        var jogak = TestFixtureFactory.jogak(20L, mogak(10L, owner), "jogak", false, java.time.LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(30L, jogak, request.getTargetDate(), DailyJogakStatus.SUCCESS);

        when(dailyJogakRepository.findActiveByJogakIdAndTargetDateWithJogakGraph(20L, request.getTargetDate()))
                .thenReturn(Optional.of(dailyJogak));
        when(postRepository.existsByDailyJogakIdAndDeletedAtIsNull(30L)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> postService.validateCreateAccess(1L, request, images, 20L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.ALREADY_EXISTS_POST);
    }

    @Test
    @DisplayName("게시글 생성에 성공하면 이미지와 게시글을 저장한다")
    void createSavesPostAfterValidationWithImages() {
        User writer = user(1L, "writer@test.com");
        PostRequestDto.CreatePostDto request = createRequest("content");
        Mogak mogak = mogak(10L, writer);
        var jogak = TestFixtureFactory.jogak(20L, mogak, "jogak", false, java.time.LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(30L, jogak, request.getTargetDate(), DailyJogakStatus.SUCCESS);
        List<PostImgRequestDto.CreatePostImgDto> uploadedImages = List.of(
                PostImgRequestDto.CreatePostImgDto.builder()
                        .imgName("thumb.png")
                        .imgUrl("https://example.com/thumb.png")
                        .thumbnail(true)
                        .build(),
                PostImgRequestDto.CreatePostImgDto.builder()
                        .imgName("body.png")
                        .imgUrl("https://example.com/body.png")
                        .thumbnail(false)
                .build()
        );

        when(dailyJogakRepository.findActiveByJogakIdAndTargetDateWithJogakGraph(20L, request.getTargetDate()))
                .thenReturn(Optional.of(dailyJogak));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(writer));
        when(postImgRepository.save(any(PostImg.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.create(1L, request, uploadedImages, 20L);

        assertThat(result.getDailyJogak().getJogak().getMogak()).isSameAs(mogak);
        assertThat(result.getUser()).isSameAs(writer);
        assertThat(result.getPostImgs()).hasSize(1);
        assertThat(result.getPostThumbnailUrl()).isEqualTo("https://example.com/thumb.png");
        verify(postImgRepository, times(2)).save(any(PostImg.class));
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("모각 목록 조회는 owner가 아니면 권한 예외를 반환한다")
    void getAllPostsThrowsInvalidPermissionWhenMogakBelongsToOtherUser() {
        User owner = user(1L, "owner@test.com");
        Mogak mogak = mogak(10L, owner);

        when(mogakRepository.findActiveById(10L)).thenReturn(Optional.of(mogak));

        Throwable throwable = catchThrowable(() -> postService.getAllPosts(2L, 0, 10L, 10));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(postRepository, never()).findAllPosts(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 상세 조회는 owner가 아니면 권한 예외를 반환한다")
    void findByIdThrowsInvalidPermissionWhenPostBelongsToOtherUser() {
        User owner = user(1L, "owner@test.com");
        Post post = post(10L, owner);

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));

        Throwable throwable = catchThrowable(() -> postService.findById(2L, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("게시글 수정은 owner가 아니면 권한 예외를 반환하고 내용은 유지된다")
    void updateThrowsInvalidPermissionWhenPostBelongsToOtherUser() {
        User owner = user(1L, "owner@test.com");
        Post post = post(10L, owner);
        PostRequestDto.UpdatePostDto request = updateRequest("updated");

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));

        Throwable throwable = catchThrowable(() -> postService.update(2L, 10L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        assertThat(post.getContents()).isEqualTo("content");
    }

    @Test
    @DisplayName("게시글 수정은 본문이 없으면 입력값 오류를 반환하고 내용은 유지된다")
    void updateThrowsInvalidParameterWhenContentsMissing() {
        User owner = user(1L, "owner@test.com");
        Post post = post(10L, owner);
        PostRequestDto.UpdatePostDto request = updateRequest(null);

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));

        Throwable throwable = catchThrowable(() -> postService.update(1L, 10L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
        assertThat(post.getContents()).isEqualTo("content");
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 삭제하면 기존 not-exist 응답을 반환한다")
    void deleteThrowsNotExistPostWhenMissing() {
        when(postRepository.findActiveById(10L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> postService.delete(1L, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_POST);
        verify(postImgRepository, never()).deleteAllByPost(any(Post.class));
        verify(postRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("이미지가 없는 게시글 삭제는 storage 삭제 없이 게시글을 soft delete 한다")
    void deleteSoftDeletesPostWithoutCallingStorageWhenImagesMissing() {
        User owner = user(1L, "owner@test.com");
        Post post = post(10L, owner);

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postImgRepository.findAllByPost(post)).thenReturn(List.of());
        when(postCommentRepository.findActiveAllByPostForCleanup(post)).thenReturn(List.of());

        postService.delete(1L, 10L);

        assertThat(post.isDeleted()).isTrue();
        verify(storageCleanupService, never()).deletePostImagesAfterCommit(anyList(), any());
        verify(postImgRepository, never()).deleteAllByPost(post);
    }

    @Test
    @DisplayName("게시글 삭제는 탈퇴 유저가 작성한 활성 댓글도 soft delete 하고 댓글 수를 감소시킨다")
    void deleteSoftDeletesActiveCommentsWrittenByDeletedUser() {
        User owner = user(1L, "owner@test.com");
        User deletedCommenter = user(2L, "deleted@test.com");
        deletedCommenter.delete();
        Post post = post(10L, owner);
        ReflectionTestUtils.setField(post, "commentCnt", 1);
        PostComment comment = PostComment.builder()
                .id(20L)
                .post(post)
                .user(deletedCommenter)
                .contents("comment")
                .build();

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postImgRepository.findAllByPost(post)).thenReturn(List.of());
        when(postCommentRepository.findActiveAllByPostForCleanup(post)).thenReturn(List.of(comment));

        postService.delete(1L, 10L);

        assertThat(comment.isDeleted()).isTrue();
        assertThat(post.getCommentCnt()).isZero();
        assertThat(post.isDeleted()).isTrue();
        verify(postCommentRepository).findActiveAllByPostForCleanup(post);
        verify(postCommentRepository, never()).findActiveAllByPost(post);
    }

    private User user(Long id, String email) {
        return TestFixtureFactory.user(id, email, "user" + id, null, null);
    }

    private Mogak mogak(Long id, User owner) {
        return TestFixtureFactory.mogak(id, owner, TestFixtureFactory.modarat(1L, owner, "modarat", "#000000"), TestFixtureFactory.category(1, "자격증"), "mogak", "#112233");
    }

    private Post post(Long id, User owner) {
        Mogak mogak = mogak(10L, owner);
        var jogak = TestFixtureFactory.jogak(20L, mogak, "jogak", false, java.time.LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(30L, jogak, false);
        return Post.builder()
                .id(id)
                .dailyJogak(dailyJogak)
                .user(owner)
                .contents("content")
                .postThumbnailUrl("https://example.com/thumb.png")
                .viewCnt(0)
                .build();
    }

    private PostRequestDto.CreatePostDto createRequest(String contents) {
        PostRequestDto.CreatePostDto request = new PostRequestDto.CreatePostDto();
        ReflectionTestUtils.setField(request, "targetDate", java.time.LocalDate.now());
        ReflectionTestUtils.setField(request, "contents", contents);
        return request;
    }

    private PostRequestDto.UpdatePostDto updateRequest(String contents) {
        PostRequestDto.UpdatePostDto request = new PostRequestDto.UpdatePostDto();
        ReflectionTestUtils.setField(request, "contents", contents);
        return request;
    }

    private MultipartFile image(String fileName) {
        return new org.springframework.mock.web.MockMultipartFile(
                "multipartFile",
                fileName,
                "image/png",
                "png".getBytes()
        );
    }
}
