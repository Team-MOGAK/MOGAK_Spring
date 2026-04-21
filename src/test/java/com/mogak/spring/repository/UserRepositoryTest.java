package com.mogak.spring.repository;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("활성 사용자만 조회한다")
    void findAllActive() {
        User active = persistUser("active@test.com", "active");
        User deleted = TestFixtureFactory.user(null, "deleted@test.com", "deleted", active.getJob(), active.getAddress());
        deleted.delete();
        entityManager.persist(deleted);
        entityManager.flush();
        entityManager.clear();

        List<User> result = userRepository.findAllActive();

        assertThat(result).extracting(User::getEmail).containsExactly("active@test.com");
    }

    private User persistUser(String email, String nickname) {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, email, nickname, job, address));
    }
}
