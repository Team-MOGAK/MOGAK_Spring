package com.mogak.spring.converter;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostConverterTest {

    @Test
    void toPostDtoExcludesSoftDeletedCommentIds() {
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
        PostComment active = PostComment.builder()
                .id(7L)
                .post(post)
                .user(user)
                .contents("active")
                .build();
        PostComment deleted = PostComment.builder()
                .id(8L)
                .post(post)
                .user(user)
                .contents("deleted")
                .build();
        deleted.delete();
        post.putComment(active);
        post.putComment(deleted);

        var result = PostConverter.toPostDto(post, List.of());

        assertThat(result.getCommentId()).containsExactly(7L);
    }
}
