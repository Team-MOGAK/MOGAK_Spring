package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.PostCommentRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.commentdto.CommentRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Test
    @DisplayName("인증 사용자가 댓글을 생성하면 댓글을 저장하고 댓글 수를 증가시킨다")
    void createUsesAuthenticationNameAndIncrementsCommentCount() {
        User writer = user(1L, "writer@test.com");
        Post post = post(10L, writer, 3, 0);
        CommentRequestDto.CreateCommentDto request = createRequest("새 댓글");

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(writer));
        when(postCommentRepository.save(any(PostComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostComment result = postCommentService.create(1L, request, 10L);

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

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findActiveByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(writer));

        PostComment result = postCommentService.update(1L, updateRequest("수정 댓글"), 10L, 100L);

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

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findActiveByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findActiveById(2L)).thenReturn(Optional.of(other));

        Throwable throwable = catchThrowable(() -> postCommentService.update(2L, updateRequest("수정 댓글"), 10L, 100L));

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

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findActiveByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findActiveById(2L)).thenReturn(Optional.of(other));

        Throwable throwable = catchThrowable(() -> postCommentService.delete(2L, 10L, 100L));

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

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findActiveByPostAndId(post, 100L)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> postCommentService.delete(1L, 10L, 100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_COMMENT);
        assertThat(post.getCommentCnt()).isEqualTo(1);
        assertThat(post.getLikeCnt()).isEqualTo(3);
        verify(userRepository, never()).findActiveById(anyLong());
        verify(postCommentRepository, never()).delete(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 삭제에 성공하면 좋아요 수는 유지하고 댓글 수만 감소시킨 뒤 댓글을 삭제한다")
    void deleteDecrementsCommentCountOnlyWhenOwnerRequests() {
        User writer = user(1L, "writer@test.com");
        Post post = post(10L, writer, 3, 2);
        PostComment comment = comment(100L, post, writer, "기존 댓글");

        when(postRepository.findActiveById(10L)).thenReturn(Optional.of(post));
        when(postCommentRepository.findActiveByPostAndId(post, 100L)).thenReturn(comment);
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(writer));

        postCommentService.delete(1L, 10L, 100L);

        assertThat(post.getCommentCnt()).isEqualTo(1);
        assertThat(post.getLikeCnt()).isEqualTo(3);
        assertThat(comment.isDeleted()).isTrue();
        verify(postCommentRepository, never()).delete(comment);
    }

    private static User user(Long id, String email) {
        return TestFixtureFactory.user(id, email, "user" + id, null, null);
    }

    private static Post post(Long id, User user, int likeCnt, int commentCnt) {
        var mogak = TestFixtureFactory.mogak(10L, user, TestFixtureFactory.modarat(1L, user, "modarat", "#000000"), TestFixtureFactory.category(1, "자격증"), "mogak", "#112233");
        var jogak = TestFixtureFactory.jogak(20L, mogak, "jogak", false, java.time.LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(30L, jogak, false);
        return Post.builder()
                .id(id)
                .dailyJogak(dailyJogak)
                .user(user)
                .contents("게시글")
                .postThumbnailUrl("thumbnail")
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
                .build();
    }

    private static CommentRequestDto.CreateCommentDto createRequest(String contents) {
        return new CommentRequestDto.CreateCommentDto(contents);
    }

    private static CommentRequestDto.UpdateCommentDto updateRequest(String contents) {
        return new CommentRequestDto.UpdateCommentDto(contents);
    }
}
