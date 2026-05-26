package com.mogak.spring.repository;

import com.mogak.spring.domain.consent.UserConsent;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    Optional<UserConsent> findByUserIdAndConsentItemId(Long userId, Long consentItemId);

    List<UserConsent> findAllByUserId(Long userId);

    List<UserConsent> findAllByUserIdAndConsentItemCodeIn(Long userId, Collection<String> codes);
}
