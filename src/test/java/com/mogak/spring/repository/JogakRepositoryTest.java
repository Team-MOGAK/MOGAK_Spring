package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
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
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class JogakRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private JogakRepository jogakRepository;

    @Test
    @DisplayName("사용자와 요일로 조회하면 해당 요일의 루틴 조각만 반환한다")
    void findDailyRoutineJogaks() {
        User user = persistUser("user@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Jogak mondayJogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "월요일 조각", true, LocalDate.now(), null, 0));
        Jogak tuesdayJogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "화요일 조각", true, LocalDate.now(), null, 0));
        Period monday = entityManager.persist(TestFixtureFactory.period("MONDAY"));
        Period tuesday = entityManager.persist(TestFixtureFactory.period("TUESDAY"));
        entityManager.persist(TestFixtureFactory.jogakPeriod(mondayJogak, monday));
        entityManager.persist(TestFixtureFactory.jogakPeriod(tuesdayJogak, tuesday));
        entityManager.flush();
        entityManager.clear();

        List<Jogak> result = jogakRepository.findDailyRoutineJogaks(user, monday.getId());

        assertThat(result).extracting(Jogak::getTitle).containsExactly("월요일 조각");
    }

    @Test
    @DisplayName("데일리 조각으로 조회하면 원본 조각을 반환한다")
    void findByDailyJogak() {
        User user = persistUser("user2@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Jogak jogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "문제풀이", false, LocalDate.now(), null, 0));
        DailyJogak dailyJogak = entityManager.persist(TestFixtureFactory.dailyJogak(null, jogak, false));
        entityManager.flush();
        entityManager.clear();

        Jogak result = jogakRepository.findByDailyJogak(dailyJogak).orElseThrow();

        assertThat(result.getTitle()).isEqualTo("문제풀이");
    }

    @Test
    @DisplayName("모각의 활성 미종료 조각 수만 계산한다")
    void countActiveOpenByMogak() {
        LocalDate today = LocalDate.of(2026, 3, 26);
        User user = persistUser("count@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Mogak otherMogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "토익", "#bbbb"));
        entityManager.persist(TestFixtureFactory.jogak(null, mogak, "종료일 없음", false, today.minusDays(1), null, 0));
        entityManager.persist(TestFixtureFactory.jogak(null, mogak, "미래 종료", false, today.minusDays(1), today.plusDays(1), 0));
        entityManager.persist(TestFixtureFactory.jogak(null, mogak, "오늘 종료", false, today.minusDays(1), today, 0));
        entityManager.persist(TestFixtureFactory.jogak(null, mogak, "과거 종료", false, today.minusDays(3), today.minusDays(1), 0));
        Jogak deleted = TestFixtureFactory.jogak(null, mogak, "삭제됨", false, today.minusDays(1), null, 0);
        deleted.delete();
        entityManager.persist(deleted);
        entityManager.persist(TestFixtureFactory.jogak(null, otherMogak, "다른 모각", false, today.minusDays(1), null, 0));
        entityManager.flush();
        entityManager.clear();

        long result = jogakRepository.countActiveOpenByMogak(mogak, today);

        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자의 활성 일회성 조각만 조회한다")
    void findActiveOneTimeJogaksByUser() {
        User user = persistUser("one-time@test.com");
        User otherUser = persistUser("one-time-other@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        Modarat otherModarat = entityManager.persist(TestFixtureFactory.modarat(null, otherUser, "서브", "#2222"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Mogak otherMogak = entityManager.persist(TestFixtureFactory.mogak(null, otherUser, otherModarat, category, "토익", "#bbbb"));
        entityManager.persist(TestFixtureFactory.jogak(null, mogak, "조회 대상", false, LocalDate.now(), null, 0));
        entityManager.persist(TestFixtureFactory.jogak(null, mogak, "루틴 제외", true, LocalDate.now(), null, 0));
        Jogak deleted = TestFixtureFactory.jogak(null, mogak, "삭제 제외", false, LocalDate.now(), null, 0);
        deleted.delete();
        entityManager.persist(deleted);
        entityManager.persist(TestFixtureFactory.jogak(null, otherMogak, "타사용자 제외", false, LocalDate.now(), null, 0));
        entityManager.flush();
        entityManager.clear();

        List<Jogak> result = jogakRepository.findActiveOneTimeJogaksByUser(user);

        assertThat(result).extracting(Jogak::getTitle).containsExactly("조회 대상");
        assertThat(result.get(0).getMogak().getTitle()).isEqualTo("정보처리기사");
        assertThat(result.get(0).getCategory().getName()).isEqualTo("자격증");
    }

    private User persistUser(String email) {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, email, "tester", job, address));
    }
}
