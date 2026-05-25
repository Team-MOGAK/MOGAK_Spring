package com.mogak.spring.service;

import com.mogak.spring.domain.user.ConsentItem;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.domain.user.UserConsent;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.ConsentItemRepository;
import com.mogak.spring.repository.UserConsentRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ConsentItemResult;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ConsentServiceImpl implements ConsentService {
    private final ConsentItemRepository consentItemRepository;
    private final UserConsentRepository userConsentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ConsentItemResult> getActiveConsentItems() {
        return consentItemRepository.findAllByActiveTrueOrderByDisplayOrderAscIdAsc().stream()
                .map(item -> new ConsentItemResult(
                        item.getId(),
                        item.getCode(),
                        item.getName(),
                        item.getDescription(),
                        item.isRequired(),
                        item.getDisplayOrder()
                ))
                .toList();
    }

    @Transactional
    @Override
    public void saveUserConsents(User user, List<UserConsentCommand> consents) {
        if (consents == null || consents.isEmpty()) {
            return;
        }
        upsertUserConsents(user, consents);
    }

    @Transactional
    @Override
    public void updateUserConsents(Long userId, List<UserConsentCommand> consents) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        if (consents == null || consents.isEmpty()) {
            return;
        }
        upsertUserConsents(user, consents);
    }

    private void upsertUserConsents(User user, List<UserConsentCommand> consents) {
        validateConsentCommands(consents);
        Map<Long, ConsentItem> consentItems = findConsentItems(consents);
        LocalDateTime now = LocalDateTime.now();

        for (UserConsentCommand consent : consents) {
            ConsentItem consentItem = consentItems.get(consent.consentItemId());
            if (consentItem == null) {
                throw new BaseException(ErrorCode.NOT_EXIST_CONSENT_ITEM);
            }
            if (!consentItem.isActive()) {
                throw new BaseException(ErrorCode.INACTIVE_CONSENT_ITEM);
            }

            UserConsent userConsent = userConsentRepository
                    .findByUserIdAndConsentItemId(user.getId(), consent.consentItemId())
                    .orElseGet(() -> UserConsent.builder()
                            .user(user)
                            .consentItem(consentItem)
                            .build());
            userConsent.update(consent.agreed(), now);
            userConsentRepository.save(userConsent);
        }
    }

    private void validateConsentCommands(List<UserConsentCommand> consents) {
        Set<Long> ids = new HashSet<>();
        for (UserConsentCommand consent : consents) {
            if (consent.consentItemId() == null || consent.agreed() == null) {
                throw new BaseException(ErrorCode.INVALID_PARAMETER_ERROR);
            }
            if (!ids.add(consent.consentItemId())) {
                throw new BaseException(ErrorCode.DUPLICATE_CONSENT_ITEM);
            }
        }
    }

    private Map<Long, ConsentItem> findConsentItems(List<UserConsentCommand> consents) {
        List<Long> ids = consents.stream()
                .map(UserConsentCommand::consentItemId)
                .toList();
        return consentItemRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(ConsentItem::getId, Function.identity()));
    }
}
