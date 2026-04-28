package com.mogak.spring.integration;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.*;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.UserService;
import com.mogak.spring.service.command.CreateJogakCommand;
import com.mogak.spring.service.result.CreateJogakResult;
import com.mogak.spring.service.result.DailyJogakListResult;
import com.mogak.spring.service.result.DailyJogakResult;
import com.mogak.spring.service.result.JogakDailyResult;
import com.mogak.spring.service.result.MogakResult;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.RoutineJogakResult;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class CoreFlowIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private MogakService mogakService;
    @Autowired private JogakService jogakService;

    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private ModaratRepository modaratRepository;
    @Autowired private MogakCategoryRepository categoryRepository;
    @Autowired private PeriodRepository periodRepository;
    @Autowired private MogakRepository mogakRepository;
    @Autowired private JogakRepository jogakRepository;
    @Autowired private DailyJogakRepository dailyJogakRepository;
    @Autowired private JogakPeriodRepository jogakPeriodRepository;
    @Autowired private EntityManager entityManager;

    @MockitoBean private AppleOAuthUserProvider appleOAuthUserProvider;
    @MockitoBean private StorageService storageService;

    @Test
    @DisplayName("회원 가입 후 모각과 조각을 생성하고 조각을 성공 처리할 수 있다")
    void registrationToJogakSuccessFlow() {
        Job job = saveJob("개발/데이터");
        Address address = saveAddress("서울특별시");
        MogakCategory category = saveCategory("자격증");
        ensureStandardPeriods();
        User rawUser = userRepository.save(new User("flow@test.com"));

        userService.create(
                rawUser.getId(),
                "flow-user",
                job.getName(),
                address.getName(),
                new ProfileImageResult(null, null)
        );

        User savedUser = userRepository.findById(rawUser.getId()).orElseThrow();
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, savedUser, "메인 모다라트", "#1111"));
        Long userId = savedUser.getId();

        MogakResult mogak = mogakService.create(
                userId,
                modarat.getId(),
                "정보처리기사",
                category.getName(),
                "필기",
                "#1234"
        );

        CreateJogakResult createdJogak = jogakService.createJogak(userId, createOneTimeJogakCommand(mogak.id(), "문제풀이", LocalDate.now()));
        JogakDailyResult started = jogakService.startJogak(userId, createdJogak.jogakId());
        JogakDailyResult succeeded = jogakService.successJogak(userId, started.dailyJogakId());

        Jogak persistedJogak = jogakRepository.findById(createdJogak.jogakId()).orElseThrow();
        DailyJogak persistedDailyJogak = dailyJogakRepository.findById(started.dailyJogakId()).orElseThrow();

        assertThat(succeeded.achievements()).isEqualTo(1);
        assertThat(persistedJogak.getAchievements()).isEqualTo(1);
        assertThat(persistedDailyJogak.getIsAchievement()).isTrue();
    }

    @Test
    @DisplayName("루틴 조각을 생성하면 오늘 조회와 미래 조회에 모두 포함된다")
    void routineJogakTodayAndFutureFlow() {
        Job job = saveJob("개발/데이터");
        Address address = saveAddress("서울특별시");
        MogakCategory category = saveCategory("자격증");
        LocalDate futureDate = LocalDate.now().plusDays(1);
        ensureStandardPeriods();

        User user = userRepository.save(TestFixtureFactory.user(null, "routine@test.com", "routine-user", job, address));
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, user, "메인 모다라트", "#1111"));
        Mogak mogak = mogakRepository.findById(mogakService.create(
                user.getId(),
                modarat.getId(),
                "루틴 모각",
                category.getName(),
                "반복",
                "#1234"
        ).id()).orElseThrow();

        CreateJogakResult createdJogak = jogakService.createJogak(user.getId(), createRoutineJogakCommand(
                mogak.getId(),
                "루틴 조각",
                LocalDate.now(),
                List.of(LocalDate.now().getDayOfWeek().name(), futureDate.getDayOfWeek().name())
        ));

        DailyJogakListResult todayResult = jogakService.getDayJogaks(user.getId(), LocalDate.now());
        DailyJogakListResult futureResult = jogakService.getDayJogaks(user.getId(), futureDate);
        List<RoutineJogakResult> routineRange = jogakService.getRoutineJogaks(user.getId(), LocalDate.now().minusDays(1), futureDate.plusDays(1));

        assertThat(createdJogak.isRoutine()).isTrue();
        assertThat(todayResult.dailyJogaks()).extracting(DailyJogakResult::title).contains("루틴 조각");
        assertThat(futureResult.dailyJogaks()).extracting(DailyJogakResult::title).contains("루틴 조각");
        assertThat(routineRange).extracting(RoutineJogakResult::title).contains("루틴 조각");
    }

    @Test
    @DisplayName("모각을 삭제하면 하위 조각과 데일리 조각도 함께 삭제된다")
    void deleteMogakCleansChildren() {
        Job job = saveJob("개발/데이터");
        Address address = saveAddress("서울특별시");
        MogakCategory category = saveCategory("자격증");
        ensureStandardPeriods();
        User user = userRepository.save(TestFixtureFactory.user(null, "delete@test.com", "delete-user", job, address));
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, user, "메인 모다라트", "#1111"));
        MogakResult mogak = mogakService.create(
                user.getId(),
                modarat.getId(),
                "삭제 대상",
                category.getName(),
                "정리",
                "#1234"
        );

        CreateJogakResult createdJogak = jogakService.createJogak(user.getId(), createOneTimeJogakCommand(mogak.id(), "임시 조각", LocalDate.now()));
        JogakDailyResult started = jogakService.startJogak(user.getId(), createdJogak.jogakId());

        mogakService.deleteMogak(user.getId(), mogak.id());
        entityManager.flush();
        entityManager.clear();

        assertThat(mogakRepository.findById(mogak.id()).orElseThrow().isDeleted()).isTrue();
        assertThat(jogakRepository.findById(createdJogak.jogakId()).orElseThrow().isDeleted()).isTrue();
        assertThat(dailyJogakRepository.findById(started.dailyJogakId()).orElseThrow().isDeleted()).isTrue();
        assertThat(jogakPeriodRepository.findAllByJogak_Id(createdJogak.jogakId())).isEmpty();
    }

    private Job saveJob(String name) {
        return jobRepository.findJobByName(name).orElseGet(() -> jobRepository.save(TestFixtureFactory.job(name)));
    }

    private Address saveAddress(String name) {
        return addressRepository.findAddressByName(name).orElseGet(() -> addressRepository.save(TestFixtureFactory.address(name)));
    }

    private MogakCategory saveCategory(String name) {
        return categoryRepository.findMogakCategoryByName(name).orElseGet(() -> categoryRepository.save(TestFixtureFactory.category(null, name)));
    }

    private void ensureStandardPeriods() {
        entityManager.createNativeQuery("delete from jogak_period").executeUpdate();
        entityManager.createNativeQuery("delete from period").executeUpdate();

        int id = 1;
        for (String day : List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")) {
            entityManager.createNativeQuery("insert into period(period_id, days) values (:id, :day)")
                    .setParameter("id", id++)
                    .setParameter("day", day)
                    .executeUpdate();
        }
        entityManager.flush();
        entityManager.clear();
    }

    private CreateJogakCommand createOneTimeJogakCommand(Long mogakId, String title, LocalDate day) {
        return new CreateJogakCommand(mogakId, title, false, null, day, null);
    }

    private CreateJogakCommand createRoutineJogakCommand(Long mogakId, String title, LocalDate today, List<String> days) {
        return new CreateJogakCommand(mogakId, title, true, days, today, null);
    }
}
