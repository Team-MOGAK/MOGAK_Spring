package com.mogak.spring.repository;

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
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class MogakRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MogakRepository mogakRepository;

    @Test
    @DisplayName("모다라트 ID로 조회하면 해당 모다라트의 모각만 반환한다")
    void findAllByModaratId() {
        User user = persistUser("user@test.com");
        Modarat firstModarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        Modarat secondModarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "서브", "#2222"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak first = entityManager.persist(TestFixtureFactory.mogak(null, user, firstModarat, category, "정보처리기사", "#aaaa"));
        Mogak second = entityManager.persist(TestFixtureFactory.mogak(null, user, secondModarat, category, "토익", "#bbbb"));
        entityManager.flush();

        List<Mogak> result = mogakRepository.findAllByModaratId(firstModarat.getId());

        assertThat(result).extracting(Mogak::getTitle).containsExactly("정보처리기사");
        assertThat(result).doesNotContain(second);
        assertThat(first.getId()).isNotNull();
    }

    @Test
    @DisplayName("조각으로 조회하면 해당 조각이 속한 모각을 반환한다")
    void findByJogak() {
        User user = persistUser("user2@test.com");
        Modarat modarat = entityManager.persist(TestFixtureFactory.modarat(null, user, "메인", "#1111"));
        MogakCategory category = entityManager.persist(TestFixtureFactory.category(null, "자격증"));
        Mogak mogak = entityManager.persist(TestFixtureFactory.mogak(null, user, modarat, category, "정보처리기사", "#aaaa"));
        Jogak jogak = entityManager.persist(TestFixtureFactory.jogak(null, mogak, "문제풀이", false, LocalDate.now(), null, 0));
        entityManager.flush();
        entityManager.clear();

        Mogak result = mogakRepository.findByJogak(jogak).orElseThrow();

        assertThat(result.getId()).isEqualTo(mogak.getId());
        assertThat(result.getTitle()).isEqualTo("정보처리기사");
    }

    private User persistUser(String email) {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, email, "tester", job, address));
    }
}
