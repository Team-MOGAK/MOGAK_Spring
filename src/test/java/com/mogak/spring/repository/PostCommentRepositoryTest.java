package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.PersistenceUnitUtil;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class PostCommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private PostCommentRepository postCommentRepository;

    @Test
    @DisplayName("한 사용자는 같은 게시글에 댓글을 여러 개 작성할 수 있다")
    void sameUserCanWriteMultipleCommentsOnPost() {
        User user = persistUser();
        Post post = persistPost(user);
        entityManager.persist(PostComment.builder()
                .post(post)
                .user(user)
                .contents("첫 번째 댓글")
                .build());
        entityManager.persist(PostComment.builder()
                .post(post)
                .user(user)
                .contents("두 번째 댓글")
                .build());
        entityManager.flush();
        entityManager.clear();

        assertThat(postCommentRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 댓글 목록 조회는 작성자를 함께 로딩한다")
    void findActiveAllByPostFetchesUser() {
        User user = persistUser();
        Post post = persistPost(user);
        entityManager.persist(PostComment.builder()
                .post(post)
                .user(user)
                .contents("댓글")
                .build());
        entityManager.flush();
        entityManager.clear();

        List<PostComment> result = postCommentRepository.findActiveAllByPost(post);

        PersistenceUnitUtil util = entityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil();
        assertThat(result).hasSize(1);
        assertThat(util.isLoaded(result.get(0), "user")).isTrue();
    }

    @Test
    @DisplayName("게시글 ID 목록으로 활성 댓글을 조회하면 게시글과 작성자를 함께 로딩한다")
    void findActiveAllByPostIdInWithUserFetchesPostAndUser() {
        User user = persistUser();
        Post firstPost = persistPost(user);
        Post secondPost = persistPost(user);
        Post otherPost = persistPost(user);
        entityManager.persist(PostComment.builder()
                .post(firstPost)
                .user(user)
                .contents("첫 번째 댓글")
                .build());
        entityManager.persist(PostComment.builder()
                .post(secondPost)
                .user(user)
                .contents("두 번째 댓글")
                .build());
        entityManager.persist(PostComment.builder()
                .post(otherPost)
                .user(user)
                .contents("제외 댓글")
                .build());
        entityManager.flush();
        entityManager.clear();

        List<PostComment> result = postCommentRepository.findActiveAllByPostIdInWithUser(
                List.of(firstPost.getId(), secondPost.getId())
        );

        PersistenceUnitUtil util = entityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil();
        assertThat(result).extracting(PostComment::getContents)
                .containsExactlyInAnyOrder("첫 번째 댓글", "두 번째 댓글");
        assertThat(result)
                .allSatisfy(comment -> {
                    assertThat(util.isLoaded(comment, "post")).isTrue();
                    assertThat(util.isLoaded(comment, "user")).isTrue();
                });
    }

    private User persistUser() {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, "comment@test.com", "commenter", job, address));
    }

    private Post persistPost(User user) {
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "모다라트", "#000000"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "모각", "#111111"));
        Jogak jogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "조각", false, LocalDate.now(), null, 0));
        DailyJogak dailyJogak = entityManager.persist(TestFixtureFactory.dailyJogak(null, jogak, false));
        return entityManager.persist(Post.builder()
                .dailyJogak(dailyJogak)
                .user(user)
                .contents("content")
                .postThumbnailUrl("thumbnail")
                .viewCnt(0)
                .build());
    }
}
