package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostResponseDtoTest {

    @Test
    @DisplayName("게시글 상세 응답은 서비스에서 조회한 댓글 ID 목록을 그대로 사용한다")
    void postDtoUsesProvidedCommentIds() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        var modarat = TestFixtureFactory.modarat(2L, user, "모다라트", "#000000");
        var mogak = TestFixtureFactory.mogak(3L, user, modarat, TestFixtureFactory.category(1, "자격증"), "모각", "#111111");
        var jogak = TestFixtureFactory.jogak(4L, mogak, "조각", false, LocalDate.now(), null, 0);
        var dailyJogak = TestFixtureFactory.dailyJogak(5L, jogak, false);
        Post post = Post.builder()
                .id(6L)
                .dailyJogak(dailyJogak)
                .user(user)
                .contents("content")
                .postThumbnailUrl("thumbnail")
                .viewCnt(0)
                .build();

        var result = PostResponseDto.PostDto.from(post, List.of(), List.of(7L));

        assertThat(result.commentId()).containsExactly(7L);
    }
}
