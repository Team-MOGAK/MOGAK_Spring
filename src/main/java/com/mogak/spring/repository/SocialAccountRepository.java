package com.mogak.spring.repository;

import com.mogak.spring.domain.user.SocialAccount;
import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);

    boolean existsByUserAndProvider(User user, SocialProvider provider);
}
