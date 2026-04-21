package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.PostLikeRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.postdto.PostLikeRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceImplTest {

    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostLikeServiceImpl postLikeService;

    @Test
    @DisplayName("좋아요가 없으면 생성하고 좋아요 수를 증가시킨다")
    void updateLikeCreatesLikeWhenMissing() {
        User user = user(1L);
        Post post = post(10L, user);
        PostLikeRequestDto.LikeDto request = likeRequest(10L);

        when(postRepository.findActiveByIdForUpdate(10L)).thenReturn(Optional.of(post));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(postLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.empty());

        String result = postLikeService.updateLike(1L, request);

        assertThat(result).isEqualTo("좋아요가 생성되었습니다");
        assertThat(post.getLikeCnt()).isEqualTo(1);
        verify(postLikeRepository).save(org.mockito.ArgumentMatchers.any(PostLike.class));
        verify(postLikeRepository, never()).deleteByPostAndUser(post, user);
        verify(postRepository).findActiveByIdForUpdate(10L);
        verify(postRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(postRepository, never()).findActiveById(org.mockito.ArgumentMatchers.anyLong());
        verify(userRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("좋아요가 이미 있으면 삭제하고 좋아요 수를 감소시킨다")
    void updateLikeDeletesLikeWhenPresent() {
        User user = user(1L);
        Post post = post(10L, user);
        ReflectionTestUtils.setField(post, "likeCnt", 1);
        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        PostLikeRequestDto.LikeDto request = likeRequest(10L);

        when(postRepository.findActiveByIdForUpdate(10L)).thenReturn(Optional.of(post));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(postLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(like));

        String result = postLikeService.updateLike(1L, request);

        assertThat(result).isEqualTo("좋아요가 삭제되었습니다");
        assertThat(post.getLikeCnt()).isZero();
        verify(postLikeRepository).deleteByPostAndUser(post, user);
        verify(postLikeRepository, never()).save(org.mockito.ArgumentMatchers.any(PostLike.class));
    }

    @Test
    @DisplayName("삭제된 게시글은 존재하지 않는 게시글로 처리한다")
    void updateLikeThrowsWhenActivePostMissing() {
        PostLikeRequestDto.LikeDto request = likeRequest(10L);

        when(postRepository.findActiveByIdForUpdate(10L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> postLikeService.updateLike(1L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_POST);
        verify(userRepository, never()).findActiveById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("좋아요 요청 게시글 ID가 없으면 입력값 오류를 반환한다")
    void updateLikeThrowsWhenPostIdMissing() {
        Throwable throwable = catchThrowable(() -> postLikeService.updateLike(1L, new PostLikeRequestDto.LikeDto()));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
        verify(postRepository, never()).findActiveByIdForUpdate(org.mockito.ArgumentMatchers.anyLong());
        verify(userRepository, never()).findActiveById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("좋아요 요청 바디가 없으면 입력값 오류를 반환한다")
    void updateLikeThrowsWhenRequestMissing() {
        Throwable throwable = catchThrowable(() -> postLikeService.updateLike(1L, null));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PARAMETER_ERROR);
        verify(postRepository, never()).findActiveByIdForUpdate(org.mockito.ArgumentMatchers.anyLong());
        verify(userRepository, never()).findActiveById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("삭제된 사용자는 존재하지 않는 사용자로 처리한다")
    void updateLikeThrowsWhenActiveUserMissing() {
        User user = user(1L);
        Post post = post(10L, user);
        PostLikeRequestDto.LikeDto request = likeRequest(10L);

        when(postRepository.findActiveByIdForUpdate(10L)).thenReturn(Optional.of(post));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> postLikeService.updateLike(1L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_USER);
        verify(postLikeRepository, never()).findByPostAndUser(
                org.mockito.ArgumentMatchers.any(Post.class),
                org.mockito.ArgumentMatchers.any(User.class)
        );
    }

    private PostLikeRequestDto.LikeDto likeRequest(Long postId) {
        PostLikeRequestDto.LikeDto request = new PostLikeRequestDto.LikeDto();
        ReflectionTestUtils.setField(request, "postId", postId);
        return request;
    }

    private User user(Long id) {
        return TestFixtureFactory.user(id, "user" + id + "@test.com", "user" + id, null, null);
    }

    private Post post(Long id, User user) {
        var modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#000000");
        var mogak = TestFixtureFactory.mogak(2L, user, modarat, TestFixtureFactory.category(1, "자격증"), "모각", "#111111");
        var jogak = TestFixtureFactory.jogak(3L, mogak, "조각", false, LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(4L, jogak, false);
        return Post.builder()
                .id(id)
                .dailyJogak(dailyJogak)
                .user(user)
                .contents("content")
                .postThumbnailUrl("thumbnail")
                .viewCnt(0)
                .build();
    }
}
