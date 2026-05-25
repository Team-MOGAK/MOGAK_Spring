package com.mogak.spring.service;

import com.mogak.spring.domain.user.ConsentItem;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.domain.user.UserConsent;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.ConsentItemRepository;
import com.mogak.spring.repository.UserConsentRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.service.command.UserConsentCommand;
import com.mogak.spring.service.result.ConsentItemResult;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentServiceImplTest {
    @Mock
    private ConsentItemRepository consentItemRepository;
    @Mock
    private UserConsentRepository userConsentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConsentServiceImpl consentService;

    @Test
    @DisplayName("활성 동의 항목을 응답 결과로 변환한다")
    void getActiveConsentItemsReturnsResults() {
        when(consentItemRepository.findAllByActiveTrueOrderByDisplayOrderAscIdAsc())
                .thenReturn(List.of(consentItem(1L, "MARKETING", true, false, 1)));

        List<ConsentItemResult> results = consentService.getActiveConsentItems();

        assertThat(results).containsExactly(new ConsentItemResult(
                1L,
                "MARKETING",
                "마케팅 수신 동의",
                null,
                false,
                1
        ));
    }

    @Test
    @DisplayName("신규 사용자 동의 상태를 저장한다")
    void saveUserConsentsCreatesUserConsent() {
        User user = TestFixtureFactory.user(10L, "user@test.com", "tester", null, null);
        ConsentItem item = consentItem(1L, "MARKETING", true, false, 1);
        when(consentItemRepository.findAllById(List.of(1L))).thenReturn(List.of(item));
        when(userConsentRepository.findByUserIdAndConsentItemId(10L, 1L)).thenReturn(Optional.empty());

        consentService.saveUserConsents(user, List.of(new UserConsentCommand(1L, true)));

        ArgumentCaptor<UserConsent> captor = ArgumentCaptor.forClass(UserConsent.class);
        verify(userConsentRepository).save(captor.capture());
        UserConsent saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getConsentItem()).isEqualTo(item);
        assertThat(saved.isAgreed()).isTrue();
        assertThat(saved.getAgreedAt()).isNotNull();
        assertThat(saved.getWithdrawnAt()).isNull();
    }

    @Test
    @DisplayName("기존 사용자 동의 상태를 철회 상태로 갱신한다")
    void updateUserConsentsUpdatesExistingUserConsent() {
        User user = TestFixtureFactory.user(10L, "user@test.com", "tester", null, null);
        ConsentItem item = consentItem(1L, "MARKETING", true, false, 1);
        UserConsent existing = UserConsent.builder()
                .user(user)
                .consentItem(item)
                .build();
        when(userRepository.findActiveById(10L)).thenReturn(Optional.of(user));
        when(consentItemRepository.findAllById(List.of(1L))).thenReturn(List.of(item));
        when(userConsentRepository.findByUserIdAndConsentItemId(10L, 1L)).thenReturn(Optional.of(existing));

        consentService.updateUserConsents(10L, List.of(new UserConsentCommand(1L, false)));

        assertThat(existing.isAgreed()).isFalse();
        assertThat(existing.getWithdrawnAt()).isNotNull();
        verify(userConsentRepository).save(existing);
    }

    @Test
    @DisplayName("중복된 동의 항목 요청은 실패한다")
    void duplicateConsentItemThrows() {
        User user = TestFixtureFactory.user(10L, "user@test.com", "tester", null, null);

        Throwable throwable = catchThrowable(() -> consentService.saveUserConsents(user, List.of(
                new UserConsentCommand(1L, true),
                new UserConsentCommand(1L, false)
        )));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.DUPLICATE_CONSENT_ITEM);
    }

    @Test
    @DisplayName("비활성 동의 항목 요청은 실패한다")
    void inactiveConsentItemThrows() {
        User user = TestFixtureFactory.user(10L, "user@test.com", "tester", null, null);
        when(consentItemRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(consentItem(1L, "MARKETING", false, false, 1)));

        Throwable throwable = catchThrowable(() -> consentService.saveUserConsents(
                user,
                List.of(new UserConsentCommand(1L, true))
        ));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INACTIVE_CONSENT_ITEM);
    }

    @Test
    @DisplayName("존재하지 않는 동의 항목 요청은 실패한다")
    void missingConsentItemThrows() {
        User user = TestFixtureFactory.user(10L, "user@test.com", "tester", null, null);
        when(consentItemRepository.findAllById(List.of(1L))).thenReturn(List.of());

        Throwable throwable = catchThrowable(() -> consentService.saveUserConsents(
                user,
                List.of(new UserConsentCommand(1L, true))
        ));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_CONSENT_ITEM);
    }

    private ConsentItem consentItem(Long id, String code, boolean active, boolean required, Integer displayOrder) {
        return ConsentItem.builder()
                .id(id)
                .code(code)
                .name("마케팅 수신 동의")
                .active(active)
                .required(required)
                .displayOrder(displayOrder)
                .build();
    }
}
