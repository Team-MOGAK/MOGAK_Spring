package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
class PostLikeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("같은 사용자는 같은 게시글에 좋아요를 중복 생성할 수 없다")
    void postLikeHasUniquePostUserConstraint() {
        User user = persistUser();
        Post post = persistPost(user);
        entityManager.persist(PostLike.builder()
                .post(post)
                .user(user)
                .build());
        entityManager.flush();

        assertThatThrownBy(() -> {
            entityManager.persist(PostLike.builder()
                    .post(post)
                    .user(user)
                    .build());
            entityManager.flush();
        })
                .isInstanceOf(org.hibernate.exception.ConstraintViolationException.class);
    }

    private User persistUser() {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, "like@test.com", "like", job, address));
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
