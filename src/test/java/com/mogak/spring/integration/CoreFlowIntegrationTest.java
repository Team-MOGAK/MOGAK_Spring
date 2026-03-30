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
import com.mogak.spring.redis.RedisService;
import com.mogak.spring.repository.*;
import com.mogak.spring.service.AwsS3Service;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.service.UserService;
import com.mogak.spring.support.SecurityContextTestHelper;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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

    @MockBean private RedisService redisService;
    @MockBean private AppleOAuthUserProvider appleOAuthUserProvider;
    @MockBean private AwsS3Service awsS3Service;

    @AfterEach
    void tearDown() {
        SecurityContextTestHelper.clear();
    }

    @Test
    @DisplayName("회원 가입 후 모각과 조각을 생성하고 조각을 성공 처리할 수 있다")
    void registrationToJogakSuccessFlow() {
        Job job = saveJob("개발/데이터");
        Address address = saveAddress("서울특별시");
        MogakCategory category = saveCategory("자격증");
        ensureStandardPeriods();
        User rawUser = userRepository.save(new User("flow@test.com"));

        userService.create(
                UserRequestDto.CreateUserDto.builder()
                        .userId(rawUser.getId())
                        .nickname("flow-user")
                        .job(job.getName())
                        .address(address.getName())
                        .build(),
                UserRequestDto.UploadImageDto.builder().build()
        );

        User savedUser = userRepository.findById(rawUser.getId()).orElseThrow();
        SecurityContextTestHelper.setAuthentication(savedUser.getEmail());
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, savedUser, "메인 모다라트", "#1111"));

        MogakResponseDto.GetMogakDto mogak = mogakService.create(MogakRequestDto.CreateDto.builder()
                .modaratId(modarat.getId())
                .title("정보처리기사")
                .bigCategory(category.getName())
                .smallCategory("필기")
                .color("#1234")
                .build());

        JogakResponseDto.CreateJogakDto createdJogak = jogakService.createJogak(createOneTimeJogakRequest(mogak.getId(), "문제풀이", LocalDate.now()));
        JogakResponseDto.JogakDailyJogakDto started = jogakService.startJogak(createdJogak.getJogakId());
        JogakResponseDto.JogakDailyJogakDto succeeded = jogakService.successJogak(started.getDailyJogakId());

        Jogak persistedJogak = jogakRepository.findById(createdJogak.getJogakId()).orElseThrow();
        DailyJogak persistedDailyJogak = dailyJogakRepository.findById(started.getDailyJogakId()).orElseThrow();

        assertThat(succeeded.getAchievements()).isEqualTo(1);
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
        SecurityContextTestHelper.setAuthentication(user.getEmail());
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, user, "메인 모다라트", "#1111"));
        Mogak mogak = mogakRepository.findById(mogakService.create(MogakRequestDto.CreateDto.builder()
                .modaratId(modarat.getId())
                .title("루틴 모각")
                .bigCategory(category.getName())
                .smallCategory("반복")
                .color("#1234")
                .build()).getId()).orElseThrow();

        JogakResponseDto.CreateJogakDto createdJogak = jogakService.createJogak(createRoutineJogakRequest(
                mogak.getId(),
                "루틴 조각",
                LocalDate.now(),
                List.of(LocalDate.now().getDayOfWeek().name(), futureDate.getDayOfWeek().name())
        ));

        JogakResponseDto.GetDailyJogakListDto todayResult = jogakService.getDayJogaks(LocalDate.now());
        JogakResponseDto.GetDailyJogakListDto futureResult = jogakService.getDayJogaks(futureDate);
        List<JogakResponseDto.GetRoutineJogakDto> routineRange = jogakService.getRoutineJogaks(LocalDate.now().minusDays(1), futureDate.plusDays(1));

        assertThat(createdJogak.getIsRoutine()).isTrue();
        assertThat(todayResult.getDailyJogaks()).extracting(JogakResponseDto.GetDailyJogakDto::getTitle).contains("루틴 조각");
        assertThat(futureResult.getDailyJogaks()).extracting(JogakResponseDto.GetDailyJogakDto::getTitle).contains("루틴 조각");
        assertThat(routineRange).extracting(JogakResponseDto.GetRoutineJogakDto::getTitle).contains("루틴 조각");
    }

    @Test
    @DisplayName("모각을 삭제하면 하위 조각과 데일리 조각도 함께 삭제된다")
    void deleteMogakCleansChildren() {
        Job job = saveJob("개발/데이터");
        Address address = saveAddress("서울특별시");
        MogakCategory category = saveCategory("자격증");
        ensureStandardPeriods();
        User user = userRepository.save(TestFixtureFactory.user(null, "delete@test.com", "delete-user", job, address));
        SecurityContextTestHelper.setAuthentication(user.getEmail());
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, user, "메인 모다라트", "#1111"));
        MogakResponseDto.GetMogakDto mogak = mogakService.create(MogakRequestDto.CreateDto.builder()
                .modaratId(modarat.getId())
                .title("삭제 대상")
                .bigCategory(category.getName())
                .smallCategory("정리")
                .color("#1234")
                .build());

        JogakResponseDto.CreateJogakDto createdJogak = jogakService.createJogak(createOneTimeJogakRequest(mogak.getId(), "임시 조각", LocalDate.now()));
        JogakResponseDto.JogakDailyJogakDto started = jogakService.startJogak(createdJogak.getJogakId());

        mogakService.deleteMogak(mogak.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(mogakRepository.findById(mogak.getId())).isEmpty();
        assertThat(jogakRepository.findById(createdJogak.getJogakId())).isEmpty();
        assertThat(dailyJogakRepository.findById(started.getDailyJogakId())).isEmpty();
        assertThat(jogakPeriodRepository.findAllByJogak_Id(createdJogak.getJogakId())).isEmpty();
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

    private JogakRequestDto.CreateJogakDto createOneTimeJogakRequest(Long mogakId, String title, LocalDate day) {
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "mogakId", mogakId);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "title", title);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "isRoutine", false);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "today", day);
        return request;
    }

    private JogakRequestDto.CreateJogakDto createRoutineJogakRequest(Long mogakId, String title, LocalDate today, List<String> days) {
        JogakRequestDto.CreateJogakDto request = new JogakRequestDto.CreateJogakDto();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "mogakId", mogakId);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "title", title);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "isRoutine", true);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "today", today);
        org.springframework.test.util.ReflectionTestUtils.setField(request, "days", days);
        return request;
    }
}
