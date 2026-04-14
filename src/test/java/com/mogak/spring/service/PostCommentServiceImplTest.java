package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.PostCommentRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.commentdto.CommentRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCommentServiceImplTest {

    @Mock
    private PostCommentRepository postCommentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostCommentServiceImpl postCommentService;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("인증 사용자가 댓글을 생성하면 댓글을 저장하고 댓글 수를 증가시킨다")
    void createUsesAuthenticationNameAndIncrementsCommentCount() {
        User writer = user(1L, "writer@test.com");
        Post post = post(10L, writer, 3, 0);
        CommentRequestDto.CreateCommentDto request = createRequest("새 댓글");

        SecurityContextTestHelper.setAuthentication("writer@test.com");
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findByEmail("writer@test.com")).thenReturn(Optional.of(writer));
        when(postCommentRepository.save(any(PostComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostComment result = postCommentService.create(request, 10L);

        assertThat(result.getPost()).isSameAs(post);
        assertThat(result.getUser()).isSameAs(writer);
        assertThat(result.getContents()).isEqualTo("새 댓글");
        assertThat(post.getCommentCnt()).isEqualTo(1);
        assertThat(post.getLikeCnt()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글 작성자가 댓글을 수정하면 댓글 내용이 변경된다")
    void updateChangesCommentWhenOwnerRequests() {
        User writer = user(1L, "writer@test.com");
        Post post = post(10L, writer, 3, 1);
        PostComment comment = comment(100L, post, writer, "기존 댓글");

        SecurityContextTestHelper.setAuthentication("writer@test.com");
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findByEmail("writer@test.com")).thenReturn(Optional.of(writer));

        PostComment result = postCommentService.update(updateRequest("수정 댓글"), 10L, 100L);

        assertThat(result.getContents()).isEqualTo("수정 댓글");
        assertThat(comment.getContents()).isEqualTo("수정 댓글");
    }

    @Test
    @DisplayName("댓글 작성자가 아닌 사용자가 댓글을 수정하면 권한 예외를 반환하고 내용은 유지된다")
    void updateThrowsInvalidPermissionWhenNotOwner() {
        User writer = user(1L, "writer@test.com");
        User other = user(2L, "other@test.com");
        Post post = post(10L, writer, 3, 1);
        PostComment comment = comment(100L, post, writer, "기존 댓글");

        SecurityContextTestHelper.setAuthentication("other@test.com");
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        Throwable throwable = catchThrowable(() -> postCommentService.update(updateRequest("수정 댓글"), 10L, 100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        assertThat(comment.getContents()).isEqualTo("기존 댓글");
    }

    @Test
    @DisplayName("댓글 작성자가 아닌 사용자가 댓글을 삭제하면 권한 예외를 반환하고 카운터와 삭제는 실행되지 않는다")
    void deleteThrowsInvalidPermissionWhenNotOwner() {
        User writer = user(1L, "writer@test.com");
        User other = user(2L, "other@test.com");
        Post post = post(10L, writer, 3, 1);
        PostComment comment = comment(100L, post, writer, "기존 댓글");

        SecurityContextTestHelper.setAuthentication("other@test.com");
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        Throwable throwable = catchThrowable(() -> postCommentService.delete(10L, 100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        assertThat(post.getCommentCnt()).isEqualTo(1);
        assertThat(post.getLikeCnt()).isEqualTo(3);
        verify(postCommentRepository, never()).delete(any(PostComment.class));
    }

    @Test
    @DisplayName("게시글과 댓글이 매칭되지 않으면 댓글 없음 예외를 반환하고 카운터는 유지된다")
    void deleteThrowsNotExistCommentWhenCommentDoesNotBelongToPost() {
        User writer = user(1L, "writer@test.com");
        Post post = post(10L, writer, 3, 1);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findByPostAndId(post, 100L)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> postCommentService.delete(10L, 100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_COMMENT);
        assertThat(post.getCommentCnt()).isEqualTo(1);
        assertThat(post.getLikeCnt()).isEqualTo(3);
        verify(userRepository, never()).findByEmail(any());
        verify(postCommentRepository, never()).delete(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 삭제에 성공하면 좋아요 수는 유지하고 댓글 수만 감소시킨 뒤 댓글을 삭제한다")
    void deleteDecrementsCommentCountOnlyWhenOwnerRequests() {
        User writer = user(1L, "writer@test.com");
        Post post = post(10L, writer, 3, 2);
        PostComment comment = comment(100L, post, writer, "기존 댓글");

        SecurityContextTestHelper.setAuthentication("writer@test.com");
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findByEmail("writer@test.com")).thenReturn(Optional.of(writer));

        postCommentService.delete(10L, 100L);

        assertThat(post.getCommentCnt()).isEqualTo(1);
        assertThat(post.getLikeCnt()).isEqualTo(3);
        verify(postCommentRepository).delete(comment);
    }

    private static User user(Long id, String email) {
        return TestFixtureFactory.user(id, email, "user" + id, null, null);
    }

    private static Post post(Long id, User user, int likeCnt, int commentCnt) {
        return Post.builder()
                .id(id)
                .user(user)
                .contents("게시글")
                .postThumbnailUrl("thumbnail")
                .validation("ACTIVE")
                .viewCnt(0)
                .likeCnt(likeCnt)
                .commentCnt(commentCnt)
                .build();
    }

    private static PostComment comment(Long id, Post post, User user, String contents) {
        return PostComment.builder()
                .id(id)
                .post(post)
                .user(user)
                .contents(contents)
                .validation("ACTIVE")
                .build();
    }

    private static CommentRequestDto.CreateCommentDto createRequest(String contents) {
        CommentRequestDto.CreateCommentDto request = new CommentRequestDto.CreateCommentDto();
        ReflectionTestUtils.setField(request, "contents", contents);
        return request;
    }

    private static CommentRequestDto.UpdateCommentDto updateRequest(String contents) {
        CommentRequestDto.UpdateCommentDto request = new CommentRequestDto.UpdateCommentDto();
        ReflectionTestUtils.setField(request, "contents", contents);
        return request;
    }
}
