package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.service.result.JogakSummaryResult;
import com.mogak.spring.service.result.MogakResult;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MogakServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ModaratRepository modaratRepository;
    @Mock private MogakRepository mogakRepository;
    @Mock private MogakCategoryRepository categoryRepository;
    @Mock private JogakRepository jogakRepository;
    @Mock private JogakService jogakService;
    @Mock private DailyJogakRepository dailyJogakRepository;

    @InjectMocks
    private MogakServiceImpl mogakService;

    @Test
    @DisplayName("유효한 모각 생성 요청을 처리하면 모각을 생성한다")
    void createSuccess() {
        Job job = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", job, address);
        Modarat modarat = TestFixtureFactory.modarat(3L, user, "메인 모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak saved = TestFixtureFactory.mogak(5L, user, modarat, category, "정보처리기사", "#112233");
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(modaratRepository.findActiveById(3L)).thenReturn(Optional.of(modarat));
        when(mogakRepository.findAllByModaratId(3L)).thenReturn(List.of());
        when(categoryRepository.findMogakCategoryByName("자격증")).thenReturn(Optional.of(category));
        when(mogakRepository.save(org.mockito.ArgumentMatchers.any(Mogak.class))).thenReturn(saved);

        MogakResult result = mogakService.create(1L, 3L, "정보처리기사", "자격증", "필기", "#112233");

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.title()).isEqualTo("정보처리기사");
        assertThat(result.bigCategory().getName()).isEqualTo("자격증");
    }

    @Test
    @DisplayName("다른 사용자의 모다라트에 모각을 생성하면 권한 오류를 반환한다")
    void createThrowsWhenModaratNotOwned() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        User otherUser = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Modarat modarat = TestFixtureFactory.modarat(3L, otherUser, "메인 모다라트", "#0000");
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(modaratRepository.findActiveById(3L)).thenReturn(Optional.of(modarat));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(
                () -> mogakService.create(1L, 3L, "정보처리기사", "자격증", null, null)
        );

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(mogakRepository, org.mockito.Mockito.never()).findAllByModaratId(3L);
        verify(mogakRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any(Mogak.class));
    }

    @Test
    @DisplayName("모다라트에 모각이 8개 있으면 새 모각을 생성할 수 없다")
    void createThrowsWhenMaxExceeded() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(3L, user, "메인 모다라트", "#0000");
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(modaratRepository.findActiveById(3L)).thenReturn(Optional.of(modarat));
        when(mogakRepository.findAllByModaratId(3L)).thenReturn(
                java.util.stream.IntStream.range(0, 8)
                        .mapToObj(i -> TestFixtureFactory.mogak((long) i, user, modarat, TestFixtureFactory.category(1, "자격증"), "모각" + i, "#1234"))
                        .collect(java.util.stream.Collectors.toList())
        );

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(
                () -> mogakService.create(1L, 3L, "정보처리기사", "자격증", null, null)
        );

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.EXCEED_MAX_MOGAK);
    }

    @Test
    @DisplayName("모각의 대분류를 변경하면 하위 조각의 카테고리도 함께 변경한다")
    void updateMogakPropagatesCategoryToJogaks() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory oldCategory = TestFixtureFactory.category(1, "자격증");
        MogakCategory newCategory = TestFixtureFactory.category(2, "직무공부");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, oldCategory, "원래 제목", "#1234");
        Jogak jogak = TestFixtureFactory.jogak(3L, mogak, "조각", false, LocalDate.now(), null, 0);

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(categoryRepository.findMogakCategoryByName("직무공부")).thenReturn(Optional.of(newCategory));
        when(jogakRepository.findAllByMogak(mogak)).thenReturn(List.of(jogak));

        MogakResult result = mogakService.updateMogak(1L, 2L, "새 제목", "직무공부", "백엔드", "#9999");

        assertThat(result.title()).isEqualTo("새 제목");
        assertThat(mogak.getBigCategory()).isEqualTo(newCategory);
        assertThat(jogak.getCategory()).isEqualTo(newCategory);
        assertThat(mogak.getSmallCategory()).isEqualTo("백엔드");
    }

    @Test
    @DisplayName("모각을 삭제하면 하위 조각 삭제를 위임한 뒤 모각을 삭제한다")
    void deleteMogakDeletesChildren() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "원래 제목", "#1234");
        Jogak first = TestFixtureFactory.jogak(10L, mogak, "첫 조각", false, LocalDate.now(), null, 0);
        Jogak second = TestFixtureFactory.jogak(11L, mogak, "둘째 조각", true, LocalDate.now(), null, 0);
        TestFixtureFactory.attachJogaks(mogak, List.of(first, second));

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.findAllByMogak(mogak)).thenReturn(List.of(first, second));

        mogakService.deleteMogak(1L, 2L);

        verify(jogakService).deleteJogakCascadeAfterParentAuthorization(10L);
        verify(jogakService).deleteJogakCascadeAfterParentAuthorization(11L);
        verify(jogakRepository).findAllByMogak(mogak);
        verify(jogakRepository).flush();
        assertThat(mogak.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("다른 사용자의 모각을 수정하면 권한 오류를 반환한다")
    void updateThrowsWhenMogakNotOwned() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        User otherUser = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, otherUser, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, otherUser, modarat, category, "원래 제목", "#1234");
        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(
                () -> mogakService.updateMogak(1L, 2L, "새 제목", "자격증", "백엔드", "#9999")
        );

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(categoryRepository, org.mockito.Mockito.never()).findMogakCategoryByName("자격증");
    }

    @Test
    @DisplayName("다른 사용자의 모각을 삭제하면 권한 오류를 반환한다")
    void deleteThrowsWhenMogakNotOwned() {
        User otherUser = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, otherUser, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, otherUser, modarat, category, "원래 제목", "#1234");

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> mogakService.deleteMogak(1L, 2L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(jogakService, org.mockito.Mockito.never()).deleteJogakCascadeAfterParentAuthorization(org.mockito.ArgumentMatchers.anyLong());
        verify(mogakRepository, org.mockito.Mockito.never()).deleteById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("다른 사용자의 모다라트 모각 목록을 조회하면 권한 오류를 반환한다")
    void getMogakListThrowsWhenModaratNotOwned() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        User otherUser = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Modarat modarat = TestFixtureFactory.modarat(3L, otherUser, "메인 모다라트", "#0000");

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(modaratRepository.findActiveById(3L)).thenReturn(Optional.of(modarat));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> mogakService.getMogakList(1L, 3L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(mogakRepository, org.mockito.Mockito.never()).findAllByModaratId(3L);
    }

    @Test
    @DisplayName("모각의 조각 목록을 조회하면 종료된 조각을 제외하고 오늘 추가 여부를 반영한다")
    void getJogaksFiltersExpiredAndMapsAlreadyAdded() {
        Job job = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", job, address);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        LocalDate day = LocalDate.of(2026, 3, 26);
        Jogak active = TestFixtureFactory.jogak(10L, mogak, "활성 조각", false, day.minusDays(1), null, 1);
        Jogak expired = TestFixtureFactory.jogak(11L, mogak, "만료 조각", false, day.minusDays(5), day.minusDays(2), 0);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, active, false);

        TestFixtureFactory.attachJogaks(mogak, List.of(active, expired));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.findAllByMogakWithFetchGraph(mogak)).thenReturn(List.of(active));
        when(dailyJogakRepository.findDailyJogaks(user, day)).thenReturn(List.of(dailyJogak));

        List<JogakSummaryResult> result = mogakService.getJogaks(1L, 2L, day);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).jogakId()).isEqualTo(10L);
        assertThat(result.get(0).isAlreadyAdded()).isTrue();
        verify(jogakRepository).findAllByMogakWithFetchGraph(mogak);
    }
}
