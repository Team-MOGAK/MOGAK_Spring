package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.DailyJogakRepository;
import com.mogak.spring.repository.JogakPeriodRepository;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.PeriodRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JogakServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private MogakRepository mogakRepository;
    @Mock private JogakRepository jogakRepository;
    @Mock private JogakPeriodRepository jogakPeriodRepository;
    @Mock private PeriodRepository periodRepository;
    @Mock private DailyJogakRepository dailyJogakRepository;

    @InjectMocks
    private JogakServiceImpl jogakService;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("일회성 조각 생성 요청을 처리하면 일회성 조각을 생성한다")
    void createJogakCreatesOneTimeJogak() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        TestFixtureFactory.attachJogaks(mogak, List.of());
        Jogak saved = TestFixtureFactory.jogak(10L, mogak, "일회성 조각", false, LocalDate.of(2026, 3, 26), null, 0);
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "mogakId", 2L);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "title", "일회성 조각");
        org.springframework.test.util.ReflectionTestUtils.setField(request, "isRoutine", false);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "today", LocalDate.of(2026, 3, 26));

        when(mogakRepository.findById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.save(any(Jogak.class))).thenReturn(saved);

        JogakResponseDto.CreateJogakDto result = jogakService.createJogak(request);

        assertThat(result.getJogakId()).isEqualTo(10L);
        assertThat(result.getIsRoutine()).isFalse();
        verify(dailyJogakRepository, times(0)).save(any(DailyJogak.class));
    }

    @Test
    @DisplayName("오늘 요일이 포함된 루틴 조각을 생성하면 오늘의 데일리 조각도 함께 생성한다")
    void createJogakCreatesRoutineAndDailyJogakForToday() {
        LocalDate today = LocalDate.of(2026, 3, 30);
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        TestFixtureFactory.attachJogaks(mogak, List.of());
        Jogak saved = TestFixtureFactory.jogak(10L, mogak, "루틴 조각", true, today, null, 0);
        Period monday = TestFixtureFactory.period(1, "MONDAY");
        Period tuesday = TestFixtureFactory.period(2, "TUESDAY");
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "mogakId", 2L);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "title", "루틴 조각");
        org.springframework.test.util.ReflectionTestUtils.setField(request, "isRoutine", true);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "today", today);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "days", List.of("MONDAY", "TUESDAY"));

        when(mogakRepository.findById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.save(any(Jogak.class))).thenReturn(saved);
        when(periodRepository.findOneByDays("MONDAY")).thenReturn(Optional.of(monday));
        when(periodRepository.findOneByDays("TUESDAY")).thenReturn(Optional.of(tuesday));

        JogakResponseDto.CreateJogakDto result = jogakService.createJogak(request);

        assertThat(result.getIsRoutine()).isTrue();
        assertThat(result.getDays()).containsExactly("MONDAY", "TUESDAY");
        verify(dailyJogakRepository).save(any(DailyJogak.class));
        verify(jogakPeriodRepository, times(2)).save(any(JogakPeriod.class));
    }

    @Test
    @DisplayName("루틴 조각 생성 요청에 반복 요일이 없으면 예외를 반환한다")
    void createJogakThrowsWhenRoutineDaysMissing() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        TestFixtureFactory.attachJogaks(mogak, List.of());
        Jogak saved = TestFixtureFactory.jogak(10L, mogak, "루틴 조각", true, LocalDate.of(2026, 3, 26), null, 0);
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "mogakId", 2L);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "title", "루틴 조각");
        org.springframework.test.util.ReflectionTestUtils.setField(request, "isRoutine", true);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "today", LocalDate.of(2026, 3, 26));

        when(mogakRepository.findById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.save(any(Jogak.class))).thenReturn(saved);

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> jogakService.createJogak(request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_VALID_PERIOD);
    }

    @Test
    @DisplayName("모각에 조각이 8개 있으면 새 조각을 생성할 수 없다")
    void createJogakThrowsWhenMaxExceeded() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        List<Jogak> existing = java.util.stream.IntStream.range(0, 8)
                .mapToObj(i -> TestFixtureFactory.jogak((long) i, mogak, "조각" + i, false, LocalDate.now(), null, 0))
                .collect(Collectors.toList());
        TestFixtureFactory.attachJogaks(mogak, existing);
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "mogakId", 2L);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "title", "새 조각");
        org.springframework.test.util.ReflectionTestUtils.setField(request, "isRoutine", false);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "today", LocalDate.of(2026, 3, 26));

        when(mogakRepository.findById(2L)).thenReturn(Optional.of(mogak));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> jogakService.createJogak(request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.EXCEED_MAX_JOGAK);
    }

    @Test
    @DisplayName("미래 날짜의 데일리 조각을 조회하면 종료일이 없는 루틴 조각도 포함한다")
    void getDayJogaksIncludesFutureRoutineWithoutEndDate() {
        LocalDate futureDay = LocalDate.now().plusDays(3);
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        Jogak routine = TestFixtureFactory.jogak(10L, mogak, "루틴 조각", true, LocalDate.now(), null, 0);

        SecurityContextTestHelper.setAuthentication("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jogakRepository.findDailyRoutineJogaks(user, futureDay.getDayOfWeek().getValue())).thenReturn(List.of(routine));

        JogakResponseDto.GetDailyJogakListDto result = jogakService.getDayJogaks(futureDay);

        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getDailyJogaks().get(0).getTitle()).isEqualTo("루틴 조각");
    }

    @Test
    @DisplayName("일회성 조각을 시작하면 오늘의 데일리 조각을 생성한다")
    void startJogakCreatesDailyJogak() {
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                TestFixtureFactory.user(1L, "user@test.com", "tester", null, null),
                TestFixtureFactory.modarat(1L, TestFixtureFactory.user(1L, "user@test.com", "tester", null, null), "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, false);

        when(jogakRepository.findById(10L)).thenReturn(Optional.of(jogak));
        when(dailyJogakRepository.findByCreatedAtBetweenAndId(LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay().plusDays(1), jogak))
                .thenReturn(Optional.empty());
        when(dailyJogakRepository.save(any(DailyJogak.class))).thenReturn(dailyJogak);

        JogakResponseDto.JogakDailyJogakDto result = jogakService.startJogak(10L);

        assertThat(result.getDailyJogakId()).isEqualTo(100L);
        assertThat(result.getAchievements()).isZero();
    }

    @Test
    @DisplayName("이미 시작한 조각은 다시 시작할 수 없다")
    void startJogakThrowsWhenAlreadyStarted() {
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                TestFixtureFactory.user(1L, "user@test.com", "tester", null, null),
                TestFixtureFactory.modarat(1L, TestFixtureFactory.user(1L, "user@test.com", "tester", null, null), "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);
        when(jogakRepository.findById(10L)).thenReturn(Optional.of(jogak));
        when(dailyJogakRepository.findByCreatedAtBetweenAndId(LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay().plusDays(1), jogak))
                .thenReturn(Optional.of(TestFixtureFactory.dailyJogak(100L, jogak, false)));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> jogakService.startJogak(10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.ALREADY_START_JOGAK);
    }

    @Test
    @DisplayName("조각을 성공 처리하면 달성 횟수가 증가한다")
    void successJogakUpdatesAchievement() {
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                TestFixtureFactory.user(1L, "user@test.com", "tester", null, null),
                TestFixtureFactory.modarat(1L, TestFixtureFactory.user(1L, "user@test.com", "tester", null, null), "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, false);

        when(dailyJogakRepository.findById(100L)).thenReturn(Optional.of(dailyJogak));
        when(jogakRepository.findByDailyJogak(dailyJogak)).thenReturn(Optional.of(jogak));

        JogakResponseDto.JogakDailyJogakDto result = jogakService.successJogak(100L);

        assertThat(result.getIsAchievement()).isTrue();
        assertThat(result.getAchievements()).isEqualTo(1);
    }

    @Test
    @DisplayName("성공하지 않은 데일리 조각은 실패 처리할 수 없다")
    void failJogakThrowsWhenDailyJogakNotSuccessful() {
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                TestFixtureFactory.user(1L, "user@test.com", "tester", null, null),
                TestFixtureFactory.modarat(1L, TestFixtureFactory.user(1L, "user@test.com", "tester", null, null), "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 1);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, false);

        when(dailyJogakRepository.findById(100L)).thenReturn(Optional.of(dailyJogak));
        when(jogakRepository.findByDailyJogak(dailyJogak)).thenReturn(Optional.of(jogak));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> jogakService.failJogak(100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_SUCCESS_DAILY_JOGAK);
    }

    @Test
    @DisplayName("루틴 일정을 조회하면 종료일이 없는 루틴 조각도 미래 일정에 포함한다")
    void getRoutineJogaksIncludesFutureRoutineWithoutEndDate() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(1);
        LocalDate end = today.plusDays(3);
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        Jogak routine = TestFixtureFactory.jogak(10L, mogak, "루틴 조각", true, today.minusDays(2), null, 0);
        Period futureDayPeriod = TestFixtureFactory.period(today.plusDays(1).getDayOfWeek().getValue(), today.plusDays(1).getDayOfWeek().name());
        JogakPeriod jogakPeriod = TestFixtureFactory.jogakPeriod(routine, futureDayPeriod);
        TestFixtureFactory.attachJogakPeriods(routine, List.of(jogakPeriod));
        DailyJogak pastDailyJogak = TestFixtureFactory.dailyJogak(100L, routine, true);
        TestFixtureFactory.setCreatedAt(pastDailyJogak, today.minusDays(1).atStartOfDay());

        SecurityContextTestHelper.setAuthentication("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(dailyJogakRepository.findByDateRange(start.atStartOfDay(), end.atStartOfDay())).thenReturn(List.of(pastDailyJogak));
        when(jogakRepository.findAllRoutineJogaksByUser(1L)).thenReturn(List.of(routine));

        List<JogakResponseDto.GetRoutineJogakDto> result = jogakService.getRoutineJogaks(start, end);

        assertThat(result).isNotEmpty();
        assertThat(result).extracting(JogakResponseDto.GetRoutineJogakDto::getTitle).contains("루틴 조각");
    }
}
