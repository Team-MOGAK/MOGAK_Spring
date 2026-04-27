package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.SocialAccount;
import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
class SocialAccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SocialAccountRepository socialAccountRepository;

    @Test
    @DisplayName("provider와 providerUserId로 소셜 계정을 조회한다")
    void findByProviderAndProviderUserId() {
        User user = persistUser("social@test.com", "social");
        entityManager.persist(persistSocialAccount(user, SocialProvider.GOOGLE, "google-123", "social@test.com"));
        entityManager.flush();
        entityManager.clear();

        SocialAccount result = socialAccountRepository.findByProviderAndProviderUserId(SocialProvider.GOOGLE, "google-123")
                .orElseThrow();

        assertThat(result.getUser().getId()).isEqualTo(user.getId());
        assertThat(result.getProvider()).isEqualTo(SocialProvider.GOOGLE);
        assertThat(result.getProviderUserId()).isEqualTo("google-123");
    }

    @Test
    @DisplayName("같은 사용자는 같은 provider의 소셜 계정을 중복 생성할 수 없다")
    void socialAccountHasUniqueUserProviderConstraint() {
        User user = persistUser("duplicate-user@test.com", "duplicate-user");
        entityManager.persist(persistSocialAccount(user, SocialProvider.APPLE, "apple-1", "duplicate-user@test.com"));
        entityManager.flush();

        assertThatThrownBy(() -> {
            entityManager.persist(persistSocialAccount(user, SocialProvider.APPLE, "apple-2", "duplicate-user@test.com"));
            entityManager.flush();
        })
                .isInstanceOf(org.hibernate.exception.ConstraintViolationException.class);
    }

    @Test
    @DisplayName("같은 provider와 providerUserId 조합의 소셜 계정을 중복 생성할 수 없다")
    void socialAccountHasUniqueProviderProviderUserIdConstraint() {
        User firstUser = persistUser("first@test.com", "first");
        User secondUser = persistUser("second@test.com", "second");
        entityManager.persist(persistSocialAccount(firstUser, SocialProvider.KAKAO, "kakao-1", "first@test.com"));
        entityManager.flush();

        assertThatThrownBy(() -> {
            entityManager.persist(persistSocialAccount(secondUser, SocialProvider.KAKAO, "kakao-1", "second@test.com"));
            entityManager.flush();
        })
                .isInstanceOf(org.hibernate.exception.ConstraintViolationException.class);
    }

    private User persistUser(String email, String nickname) {
        Job job = entityManager.persist(TestFixtureFactory.job("개발/데이터"));
        Address address = entityManager.persist(TestFixtureFactory.address("서울특별시"));
        return entityManager.persist(TestFixtureFactory.user(null, email, nickname, job, address));
    }

    private SocialAccount persistSocialAccount(User user, SocialProvider provider, String providerUserId, String email) {
        return SocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .email(email)
                .build();
    }
}
