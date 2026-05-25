package com.mogak.spring.repository;

import com.mogak.spring.domain.user.UserConsent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    Optional<UserConsent> findByUserIdAndConsentItemId(Long userId, Long consentItemId);

    List<UserConsent> findAllByUserId(Long userId);
}
