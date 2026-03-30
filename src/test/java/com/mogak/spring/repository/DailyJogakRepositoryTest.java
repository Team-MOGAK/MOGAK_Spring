package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class DailyJogakRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private DailyJogakRepository dailyJogakRepository;

    @Test
    @DisplayName("사용자와 날짜 범위로 조회하면 해당 범위의 데일리 조각만 반환한다")
    void findDailyJogaks() {
        User user = persistUser("user@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Jogak jogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "문제풀이", false, LocalDate.now(), null, 0));
        DailyJogak target = entityManager.persist(TestFixtureFactory.dailyJogak(null, jogak, false));
        TestFixtureFactory.setCreatedAt(target, LocalDate.of(2026, 3, 26).atStartOfDay());

        User otherUser = persistUser("other@test.com");
        Modarat otherModarat = entityManager.persist(TestFixtureFactory.modarat(null, otherUser, "서브", "#2222"));
        Mogak otherMogak = entityManager.persist(TestFixtureFactory.mogak(null, otherUser, otherModarat, category, "토익", "#bbbb"));
        Jogak otherJogak = entityManager.persist(TestFixtureFactory.jogak(null, otherMogak, "영단어", false, LocalDate.now(), null, 0));
        DailyJogak otherDailyJogak = entityManager.persist(TestFixtureFactory.dailyJogak(null, otherJogak, false));
        TestFixtureFactory.setCreatedAt(otherDailyJogak, LocalDate.of(2026, 3, 26).atStartOfDay());

        entityManager.flush();
        entityManager.clear();

        List<DailyJogak> result = dailyJogakRepository.findDailyJogaks(
                user,
                LocalDate.of(2026, 3, 26).atStartOfDay(),
                LocalDate.of(2026, 3, 27).atStartOfDay()
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("문제풀이");
    }

    @Test
    @DisplayName("날짜 범위와 조각으로 조회하면 해당 데일리 조각을 반환한다")
    void findByCreatedAtBetweenAndId() {
        User user = persistUser("user2@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Jogak jogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "문제풀이", false, LocalDate.now(), null, 0));
        DailyJogak dailyJogak = entityManager.persist(TestFixtureFactory.dailyJogak(null, jogak, false));
        TestFixtureFactory.setCreatedAt(dailyJogak, LocalDate.of(2026, 3, 26).atTime(10, 0));
        entityManager.flush();
        entityManager.clear();

        DailyJogak result = dailyJogakRepository.findByCreatedAtBetweenAndId(
                LocalDate.of(2026, 3, 26).atStartOfDay(),
                LocalDate.of(2026, 3, 27).atStartOfDay(),
                jogak
        ).orElseThrow();

        assertThat(result.getTitle()).isEqualTo("문제풀이");
    }

    private User persistUser(String email) {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, email, "tester", job, address));
    }
}
