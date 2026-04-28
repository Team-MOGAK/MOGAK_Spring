package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.jogak.DailyJogakStatus;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JogakServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private MogakRepository mogakRepository;
    @Mock private JogakRepository jogakRepository;
    @Mock private JogakPeriodRepository jogakPeriodRepository;
    @Mock private PeriodRepository periodRepository;
    @Mock private DailyJogakRepository dailyJogakRepository;
    @Mock private PostRepository postRepository;
    @Mock private PostLikeRepository postLikeRepository;
    @Mock private PostCommentRepository postCommentRepository;
    @Mock private PostImgRepository postImgRepository;
    @Mock private StorageCleanupService storageCleanupService;

    @InjectMocks
    private JogakServiceImpl jogakService;

    @Test
    @DisplayName("일회성 조각 생성 요청을 처리하면 일회성 조각을 생성한다")
    void createJogakCreatesOneTimeJogak() {
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        TestFixtureFactory.attachJogaks(mogak, List.of());
        Jogak saved = TestFixtureFactory.jogak(10L, mogak, "일회성 조각", false, LocalDate.of(2026, 3, 26), null, 0);
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto(
                2L, "일회성 조각", false, null, LocalDate.of(2026, 3, 26), null
        );

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.save(any(Jogak.class))).thenReturn(saved);

        JogakResponseDto.CreateJogakDto result = jogakService.createJogak(1L, request);

        assertThat(result.jogakId()).isEqualTo(10L);
        assertThat(result.isRoutine()).isFalse();
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
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto(
                2L, "루틴 조각", true, List.of("MONDAY", "TUESDAY"), today, null
        );

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.save(any(Jogak.class))).thenReturn(saved);
        when(periodRepository.findOneByDays("MONDAY")).thenReturn(Optional.of(monday));
        when(periodRepository.findOneByDays("TUESDAY")).thenReturn(Optional.of(tuesday));

        JogakResponseDto.CreateJogakDto result = jogakService.createJogak(1L, request);

        assertThat(result.isRoutine()).isTrue();
        assertThat(result.days()).containsExactly("MONDAY", "TUESDAY");
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
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto(
                2L, "루틴 조각", true, null, LocalDate.of(2026, 3, 26), null
        );

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.save(any(Jogak.class))).thenReturn(saved);

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.createJogak(1L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_VALID_PERIOD);
    }

    @Test
    @DisplayName("타인 모각에 조각 생성 요청을 하면 권한 오류를 반환한다")
    void createJogakThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, owner, modarat, category, "모각", "#1234");
        TestFixtureFactory.attachJogaks(mogak, List.of());
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto(
                2L, "조각", false, null, LocalDate.of(2026, 3, 26), null
        );

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.createJogak(other.getId(), request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(jogakRepository, times(0)).save(any(Jogak.class));
    }

    @Test
    @DisplayName("존재하지 않는 모각에 조각 생성 요청을 하면 기존 not-exist 응답을 반환한다")
    void createJogakThrowsWhenMogakMissing() {
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto(
                999L, "조각", false, null, LocalDate.of(2026, 3, 26), null
        );

        when(mogakRepository.findActiveById(999L)).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.createJogak(1L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_MOGAK);
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
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto(
                2L, "새 조각", false, null, LocalDate.of(2026, 3, 26), null
        );

        when(mogakRepository.findActiveById(2L)).thenReturn(Optional.of(mogak));
        when(jogakRepository.countActiveOpenByMogak(mogak, LocalDate.now())).thenReturn(8L);

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.createJogak(1L, request));

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

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(jogakRepository.findDailyRoutineJogaks(user, futureDay.getDayOfWeek().getValue())).thenReturn(List.of(routine));

        JogakResponseDto.GetDailyJogakListDto result = jogakService.getDayJogaks(1L, futureDay);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.dailyJogaks().get(0).title()).isEqualTo("루틴 조각");
    }

    @Test
    @DisplayName("일회성 조각 조회는 활성 일회성 조각을 repository 쿼리로 조회한다")
    void getDailyJogaksUsesActiveOneTimeJogakQuery() {
        LocalDate day = LocalDate.of(2026, 3, 26);
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", null, null);
        Modarat modarat = TestFixtureFactory.modarat(1L, user, "모다라트", "#0000");
        MogakCategory category = TestFixtureFactory.category(1, "자격증");
        Mogak mogak = TestFixtureFactory.mogak(2L, user, modarat, category, "모각", "#1234");
        Jogak jogak = TestFixtureFactory.jogak(10L, mogak, "일회성 조각", false, day, null, 0);

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(jogakRepository.findActiveOneTimeJogaksByUser(user)).thenReturn(List.of(jogak));
        when(dailyJogakRepository.findDailyJogaks(user, day)).thenReturn(List.of());

        JogakResponseDto.GetOneTimeJogakListDto result = jogakService.getDailyJogaks(1L, day);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.jogaks().get(0).title()).isEqualTo("일회성 조각");
        verify(jogakRepository).findActiveOneTimeJogaksByUser(user);
        verify(mogakRepository, never()).findAllByUser(any(User.class));
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

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));
        when(dailyJogakRepository.findActiveByJogakAndTargetDate(jogak, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(dailyJogakRepository.save(any(DailyJogak.class))).thenReturn(dailyJogak);

        JogakResponseDto.JogakDailyJogakDto result = jogakService.startJogak(1L, 10L);

        assertThat(result.dailyJogakId()).isEqualTo(100L);
        assertThat(result.achievements()).isZero();
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
        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));
        when(dailyJogakRepository.findActiveByJogakAndTargetDate(jogak, LocalDate.now()))
                .thenReturn(Optional.of(TestFixtureFactory.dailyJogak(100L, jogak, false)));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.startJogak(1L, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.ALREADY_START_JOGAK);
    }

    @Test
    @DisplayName("타인 조각 시작 요청을 하면 권한 오류를 반환한다")
    void startJogakThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.startJogak(other.getId(), 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(dailyJogakRepository, times(0)).save(any(DailyJogak.class));
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

        when(dailyJogakRepository.findActiveByIdWithJogakGraph(100L)).thenReturn(Optional.of(dailyJogak));

        JogakResponseDto.JogakDailyJogakDto result = jogakService.successJogak(1L, 100L);

        assertThat(result.isAchievement()).isTrue();
        assertThat(result.achievements()).isEqualTo(1);
    }

    @Test
    @DisplayName("성공하지 않은 데일리 조각도 실패 처리될 수 있다")
    void failJogakThrowsWhenDailyJogakNotSuccessful() {
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                TestFixtureFactory.user(1L, "user@test.com", "tester", null, null),
                TestFixtureFactory.modarat(1L, TestFixtureFactory.user(1L, "user@test.com", "tester", null, null), "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 1);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, false);

        when(dailyJogakRepository.findActiveByIdWithJogakGraph(100L)).thenReturn(Optional.of(dailyJogak));

        JogakResponseDto.JogakDailyJogakDto result = jogakService.failJogak(1L, 100L);

        assertThat(result.isAchievement()).isFalse();
        assertThat(result.achievements()).isEqualTo(1);
    }

    @Test
    @DisplayName("타인 데일리 조각 성공 요청을 하면 권한 오류를 반환한다")
    void successJogakThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, false);

        when(dailyJogakRepository.findActiveByIdWithJogakGraph(100L)).thenReturn(Optional.of(dailyJogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.successJogak(other.getId(), 100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("타인 데일리 조각 실패 요청을 하면 권한 오류를 반환한다")
    void failJogakThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 1);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, false);

        when(dailyJogakRepository.findActiveByIdWithJogakGraph(100L)).thenReturn(Optional.of(dailyJogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.failJogak(other.getId(), 100L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("타인 조각 상세 조회를 하면 권한 오류를 반환한다")
    void getJogakDetailThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", true, LocalDate.now(), null, 0);

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.getJogakDetail(other.getId(), 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("존재하지 않는 조각 상세 조회는 기존 not-exist 응답을 반환한다")
    void getJogakDetailThrowsWhenJogakMissing() {
        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.getJogakDetail(1L, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_JOGAK);
    }

    @Test
    @DisplayName("타인 조각 수정 요청을 하면 권한 오류를 반환한다")
    void updateJogakThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);
        JogakRequestDto.UpdateJogakDto request = new JogakRequestDto.UpdateJogakDto("수정", false, null, null);

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.updateJogak(other.getId(), 10L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("존재하지 않는 조각 수정은 기존 not-exist 응답을 반환한다")
    void updateJogakThrowsWhenJogakMissing() {
        JogakRequestDto.UpdateJogakDto request = new JogakRequestDto.UpdateJogakDto("수정", false, null, null);

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.updateJogak(1L, 10L, request));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_JOGAK);
    }

    @Test
    @DisplayName("조각 수정은 이미 생성된 데일리 조각의 제목과 루틴 여부 스냅샷을 바꾸지 않는다")
    void updateJogakKeepsExistingDailyJogakSnapshot() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        Mogak mogak = TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234");
        Jogak jogak = TestFixtureFactory.jogak(10L, mogak, "기존 조각", true, LocalDate.now(), null, 0);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, LocalDate.now(), DailyJogakStatus.PENDING);
        JogakRequestDto.UpdateJogakDto request = new JogakRequestDto.UpdateJogakDto("수정 조각", false, null, null);
        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));

        JogakResponseDto.CreateJogakDto result = jogakService.updateJogak(owner.getId(), 10L, request);

        assertThat(result.title()).isEqualTo("수정 조각");
        assertThat(result.isRoutine()).isFalse();
        assertThat(dailyJogak.getTitle()).isEqualTo("기존 조각");
        assertThat(dailyJogak.getIsRoutine()).isTrue();
    }

    @Test
    @DisplayName("타인 조각 삭제 요청을 하면 권한 오류를 반환한다")
    void deleteJogakThrowsWhenOwnerMismatch() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        User other = TestFixtureFactory.user(2L, "other@test.com", "other", null, null);
        Jogak jogak = TestFixtureFactory.jogak(10L, TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234"), "조각", false, LocalDate.now(), null, 0);

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.deleteJogak(other.getId(), 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(jogakRepository, times(0)).deleteById(10L);
    }

    @Test
    @DisplayName("조각 삭제는 데일리 조각 아래 회고와 댓글도 soft delete 하고 이미지는 hard delete 한다")
    void deleteJogakCascadeSoftDeletesDailyJogakPostAndComments() {
        User owner = TestFixtureFactory.user(1L, "owner@test.com", "owner", null, null);
        Mogak mogak = TestFixtureFactory.mogak(2L,
                owner,
                TestFixtureFactory.modarat(1L, owner, "모다라트", "#0000"),
                TestFixtureFactory.category(1, "자격증"),
                "모각",
                "#1234");
        Jogak jogak = TestFixtureFactory.jogak(10L, mogak, "조각", false, LocalDate.now(), null, 0);
        DailyJogak dailyJogak = TestFixtureFactory.dailyJogak(100L, jogak, LocalDate.now(), DailyJogakStatus.SUCCESS);
        Post post = Post.builder()
                .id(200L)
                .dailyJogak(dailyJogak)
                .user(owner)
                .contents("회고")
                .postThumbnailUrl("thumbnail")
                .viewCnt(0)
                .commentCnt(1)
                .build();
        User deletedCommenter = TestFixtureFactory.user(3L, "deleted@test.com", "deleted", null, null);
        deletedCommenter.delete();
        PostComment comment = PostComment.builder()
                .id(300L)
                .post(post)
                .user(deletedCommenter)
                .contents("댓글")
                .build();
        PostImg postImg = PostImg.builder()
                .id(400L)
                .post(post)
                .imgName("img.png")
                .imgUrl("https://example.com/img.png")
                .build();

        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.of(jogak));
        when(dailyJogakRepository.findActiveAllByJogak(jogak)).thenReturn(List.of(dailyJogak));
        when(postRepository.findActiveAllByDailyJogakId(100L)).thenReturn(List.of(post));
        when(postCommentRepository.findActiveAllByPostForCleanup(post)).thenReturn(List.of(comment));
        when(postImgRepository.findAllByPost(post)).thenReturn(List.of(postImg));

        jogakService.deleteJogak(owner.getId(), 10L);

        assertThat(comment.isDeleted()).isTrue();
        assertThat(post.isDeleted()).isTrue();
        assertThat(dailyJogak.isDeleted()).isTrue();
        assertThat(jogak.isDeleted()).isTrue();
        assertThat(post.getCommentCnt()).isZero();
        verify(storageCleanupService).deletePostImagesAfterCommit(List.of(postImg), "img");
        verify(postLikeRepository).deleteAllByPost(post);
        verify(postImgRepository).deleteAllByPost(post);
        verify(jogakPeriodRepository).deleteAllByJogakId(10L);
    }

    @Test
    @DisplayName("존재하지 않는 조각 삭제는 기존 not-exist 응답을 반환한다")
    void deleteJogakThrowsWhenJogakMissing() {
        when(jogakRepository.findActiveById(10L)).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> jogakService.deleteJogak(1L, 10L));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_EXIST_JOGAK);
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
        DailyJogak pastDailyJogak = TestFixtureFactory.dailyJogak(100L, routine, today.minusDays(1), DailyJogakStatus.SUCCESS);

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));
        when(dailyJogakRepository.findDailyJogaksBetween(user, start, end)).thenReturn(List.of(pastDailyJogak));
        when(jogakRepository.findAllRoutineJogaksByUser(1L)).thenReturn(List.of(routine));

        List<JogakResponseDto.GetRoutineJogakDto> result = jogakService.getRoutineJogaks(1L, start, end);

        assertThat(result).isNotEmpty();
        assertThat(result).extracting(JogakResponseDto.GetRoutineJogakDto::title).contains("루틴 조각");
    }
}
