package com.mogak.spring.service;

import com.mogak.spring.domain.consent.ConsentItem;
import com.mogak.spring.domain.consent.UserConsent;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.ConsentItemRepository;
import com.mogak.spring.repository.UserConsentRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.service.command.MarketingConsentCommand;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ConsentItemResult;
import com.mogak.spring.service.result.MarketingConsentResult;
import java.util.Collection;
import java.util.HashMap;
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
    private static final String MARKETING_CODE = "MARKETING";
    private static final String ADVERTISEMENT_CODE = "ADVERTISEMENT";
    private static final List<String> MARKETING_CONSENT_CODES = List.of(
            MARKETING_CODE,
            ADVERTISEMENT_CODE
    );

    private final ConsentItemRepository consentItemRepository;
    private final UserConsentRepository userConsentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ConsentItemResult> getActiveConsentItems() {
        return consentItemRepository.findAllByActiveTrueOrderByIdAsc().stream()
                .map(item -> new ConsentItemResult(
                        item.getId(),
                        item.getCode(),
                        item.getName(),
                        item.getDescription(),
                        item.isRequired()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public MarketingConsentResult getMarketingConsent(Long userId) {
        validateActiveUser(userId);
        return getCurrentMarketingConsent(userId);
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
        User user = validateActiveUser(userId);
        if (consents == null || consents.isEmpty()) {
            return;
        }
        upsertUserConsents(user, consents);
    }

    @Transactional
    @Override
    public MarketingConsentResult updateMarketingConsent(Long userId, MarketingConsentCommand command) {
        if (command == null || command.isEmpty()) {
            throw new BaseException(ErrorCode.INVALID_PARAMETER_ERROR);
        }

        User user = validateActiveUser(userId);
        List<String> requestedCodes = requestedCodes(command);
        Map<String, ConsentItem> consentItems = findActiveConsentItemsByCode(requestedCodes);
        Map<String, UserConsent> userConsents = findUserConsentsByCode(userId, requestedCodes);
        LocalDateTime now = LocalDateTime.now();

        if (command.marketingAgreed() != null) {
            updateMarketingConsent(user, command.marketingAgreed(), MARKETING_CODE, consentItems, userConsents, now);
        }
        if (command.advertisementAgreed() != null) {
            updateMarketingConsent(user, command.advertisementAgreed(), ADVERTISEMENT_CODE, consentItems, userConsents, now);
        }

        return getCurrentMarketingConsent(userId);
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

            userConsentRepository.findByUserIdAndConsentItemId(user.getId(), consent.consentItemId())
                    .ifPresentOrElse(
                            userConsent -> userConsent.update(consent.agreed(), now),
                            () -> {
                                UserConsent userConsent = UserConsent.builder()
                                        .user(user)
                                        .consentItem(consentItem)
                                        .build();
                                userConsent.update(consent.agreed(), now);
                                userConsentRepository.save(userConsent);
                            }
                    );
        }
    }

    private void validateConsentCommands(List<UserConsentCommand> consents) {
        Set<Long> ids = new HashSet<>();
        for (UserConsentCommand consent : consents) {
            if (consent == null || consent.consentItemId() == null || consent.agreed() == null) {
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

    private User validateActiveUser(Long userId) {
        return userRepository.findActiveById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
    }

    private MarketingConsentResult getCurrentMarketingConsent(Long userId) {
        Map<String, UserConsent> userConsents = findUserConsentsByCode(userId, MARKETING_CONSENT_CODES);
        return new MarketingConsentResult(
                agreed(userConsents, MARKETING_CODE),
                agreed(userConsents, ADVERTISEMENT_CODE)
        );
    }

    private boolean agreed(Map<String, UserConsent> userConsents, String code) {
        UserConsent userConsent = userConsents.get(code);
        return userConsent != null && userConsent.isAgreed();
    }

    private List<String> requestedCodes(MarketingConsentCommand command) {
        return MARKETING_CONSENT_CODES.stream()
                .filter(code -> {
                    if (MARKETING_CODE.equals(code)) {
                        return command.marketingAgreed() != null;
                    }
                    if (ADVERTISEMENT_CODE.equals(code)) {
                        return command.advertisementAgreed() != null;
                    }
                    return false;
                })
                .toList();
    }

    private Map<String, ConsentItem> findActiveConsentItemsByCode(Collection<String> codes) {
        Map<String, ConsentItem> consentItems = consentItemRepository.findAllByCodeInAndActiveTrue(codes).stream()
                .collect(Collectors.toMap(ConsentItem::getCode, Function.identity()));
        if (consentItems.size() != codes.size()) {
            throw new BaseException(ErrorCode.NOT_EXIST_CONSENT_ITEM);
        }
        return consentItems;
    }

    private Map<String, UserConsent> findUserConsentsByCode(Long userId, Collection<String> codes) {
        Map<String, UserConsent> userConsents = new HashMap<>();
        userConsentRepository.findAllByUserIdAndConsentItemCodeIn(userId, codes)
                .forEach(userConsent -> userConsents.put(userConsent.getConsentItem().getCode(), userConsent));
        return userConsents;
    }

    private void updateMarketingConsent(
            User user,
            Boolean agreed,
            String code,
            Map<String, ConsentItem> consentItems,
            Map<String, UserConsent> userConsents,
            LocalDateTime now
    ) {
        UserConsent userConsent = userConsents.get(code);
        if (userConsent == null) {
            userConsent = UserConsent.builder()
                    .user(user)
                    .consentItem(consentItems.get(code))
                    .build();
            userConsentRepository.save(userConsent);
        }
        userConsent.update(agreed, now);
    }
}
