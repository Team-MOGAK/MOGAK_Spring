package com.mogak.spring.service;

import com.mogak.spring.domain.common.Weeks;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.DailyJogakStatus;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.JogakException;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.service.command.CreateJogakCommand;
import com.mogak.spring.service.command.UpdateJogakCommand;
import com.mogak.spring.service.result.CreateJogakResult;
import com.mogak.spring.service.result.DailyJogakListResult;
import com.mogak.spring.service.result.DailyJogakResult;
import com.mogak.spring.service.result.JogakDailyResult;
import com.mogak.spring.service.result.JogakDetailResult;
import com.mogak.spring.service.result.OneTimeJogakListResult;
import com.mogak.spring.service.result.OneTimeJogakResult;
import com.mogak.spring.service.result.RoutineJogakResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JogakServiceImpl implements JogakService {

    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;
    private final JogakPeriodRepository jogakPeriodRepository;
    private final PeriodRepository periodRepository;
    private final DailyJogakRepository dailyJogakRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostImgRepository postImgRepository;
    private final StorageCleanupService storageCleanupService;
    private static final String DIR_NAME = "img";

    /**
     * 자정에 Ongoing인 모든 모각 생성
     */
    @Transactional
    public void createRoutineJogakToday() {
        LocalDate today = LocalDate.now();
        for (User user: userRepository.findAllActive()) {
            List<Jogak> jogaks  = jogakRepository.findDailyRoutineJogaks(user, Weeks.getTodayNum());
            for (Jogak jogak : jogaks) {
                if (dailyJogakRepository.findActiveByJogakAndTargetDate(jogak, today).isEmpty()) {
                    dailyJogakRepository.save(DailyJogak.create(jogak, today));
                }
            }
        }
    }

//    /**
//     * 자정 1분까지 시작하지 않은 조각 실패 처리
//     * +) 자정엔 조각 생성 스케줄이 있어서 1분 이후에 처리
//     */
//    @Transactional
//    public void failRoutineJogakAtMidnight() {
//        List<Jogak> jogaks = jogakRepository.findJogakByState(null);
//        for (Jogak jogak : jogaks) {
//            jogak.updateState(JogakState.FAIL);
//        }
//    }

    /**
     * 새벽 4시까지 종료를 누르지 않은 조각 실패 처리
     */
//    @Transactional
//    public void failJogakAtFour() {
//        List<Jogak> jogaks = jogakRepository.findJogakIsOngoingYesterday(JogakState.ONGOING.name());
//        for (Jogak jogak : jogaks) {
//            jogak.updateState(JogakState.FAIL);
//        }
//    }

    @Transactional
    @Override
    public CreateJogakResult createJogak(Long userId, CreateJogakCommand command) {
        Mogak mogak = mogakRepository.findActiveById(command.mogakId())
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        validateMogakOwner(userId, mogak);

        // 조각 갯수 검증
        if (!validateJogakNum(mogak)) {
            throw new BaseException(ErrorCode.EXCEED_MAX_JOGAK);
        }
        if (command.today() == null) {
            throw new JogakException(ErrorCode.NOT_VALID_START_DATE);
        }
        Jogak jogak = jogakRepository.save(
                Jogak.create(mogak, command.title(), command.isRoutine(), command.today(), command.endDate())
        );
        validatePeriod(Optional.ofNullable(command.isRoutine()), Optional.ofNullable(command.days()));

        // 루틴이 존재할 경우
        if (Boolean.TRUE.equals(command.isRoutine())) {
            List<Period> periods = new ArrayList<>();
            List<String> requestDays = command.days();
            if (requestDays == null) {
                throw new BaseException(ErrorCode.NOT_EXIST_ROUTINES);
            }
            List<String> days = new ArrayList<>();
            // 반복주기 추출
            for (String day: requestDays) {
                Period period = periodRepository.findOneByDays(day)
                        .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_DAY));
                periods.add(period);
                // 주기와 오늘이 일치하는 경우
                if (dateToNum(command.today()) == period.getId()) {
                    dailyJogakRepository.save(DailyJogak.create(jogak, command.today()));
                }
            }
            // 다대다-조각주기 저장
            for (Period period: periods) {
                jogakPeriodRepository.save(JogakPeriod.of(period, jogak));
                days.add(period.getDays());
            }
            return CreateJogakResult.from(jogak, days);
        }
        // 루틴이 없는 경우
        return CreateJogakResult.from(jogak);
    }

    // 모각의 조각 개수 검증
    private boolean validateJogakNum(Mogak mogak) {
        return jogakRepository.countActiveOpenByMogak(mogak, LocalDate.now()) < 8;
    }

    @Transactional
    @Override
    public CreateJogakResult updateJogak(Long userId, Long jogakId, UpdateJogakCommand command) {
        Jogak jogak = jogakRepository.findActiveById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        validateJogakOwner(userId, jogak);

        validatePeriod(Optional.ofNullable(command.isRoutine()), Optional.ofNullable(command.days()));
        jogak.update(command.title(), command.isRoutine(), command.endDate());

        if (command.days() != null) {
            updateJogakPeriod(jogak, command.days());
        }
        if (command.isRoutine() != null && !command.isRoutine()) {
            jogakPeriodRepository.deleteAllByJogakId(jogak.getId());
        }

        return CreateJogakResult.from(jogak);
    }

    private void validatePeriod(Optional<Boolean> isRoutineOptional, Optional<List<String>> daysOptional) {
        isRoutineOptional.ifPresent(isRoutine -> {
            if (isRoutine && daysOptional.isEmpty()) {
                throw new JogakException(ErrorCode.NOT_VALID_PERIOD);
            }
        });
        daysOptional.ifPresent(days -> {
            if (isRoutineOptional.isEmpty() || !isRoutineOptional.get()) {
                throw new JogakException(ErrorCode.NOT_VALID_PERIOD);
            }
        });
    }

    /**
     * 모각주기 업데이트 메소드
     * */
    private void updateJogakPeriod(Jogak jogak, List<String> days) {
        List<Period> periods = new ArrayList<>();
        int todayNum = dateToNum(LocalDate.now());

        for (String day : days) {
            Period period = periodRepository.findOneByDays(day)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_DAY));
            periods.add(period);

            // 주기와 오늘이 일치하는 경우
            if (todayNum == period.getId()) {
                boolean isPeriodAlreadyAssigned = jogak.getJogakPeriods().stream()
                        .anyMatch(jogakPeriod -> jogakPeriod.getPeriod().equals(period));

                // 오늘 날짜에 해당하는 Period가 JogakPeriods에 존재하지 않는 경우에만 새로운 DailyJogak 저장
                LocalDate today = LocalDate.now();
                if (!isPeriodAlreadyAssigned && dailyJogakRepository.findActiveByJogakAndTargetDate(jogak, today).isEmpty()) {
                    dailyJogakRepository.save(DailyJogak.create(jogak, today));
                }
            }
        }

        List<JogakPeriod> mogakPeriods = jogakPeriodRepository.findAllByJogak_Id(jogak.getId());
        int periodSize = periods.size();
        int mpSize = mogakPeriods.size();

        IntStream.range(0, Math.min(mpSize, periodSize))
                .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
        if (mpSize > periodSize) {
            IntStream.range(periodSize, mpSize)
                    .forEach(i -> jogakPeriodRepository.delete(mogakPeriods.get(i)));
        } else {
            IntStream.range(mpSize, periodSize)
                    .forEach(i -> jogakPeriodRepository.save(JogakPeriod.of(periods.get(i), jogak)));
        }
    }

    @Override
    public OneTimeJogakListResult getDailyJogaks(Long userId, LocalDate day) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        List<Jogak> jogakList = jogakRepository.findActiveOneTimeJogaksByUser(user);
        List<DailyJogak> dailyJogak = dailyJogakRepository.findDailyJogaks(user, day);
        List<OneTimeJogakResult> results = jogakList.stream()
                .map(jogak -> OneTimeJogakResult.from(jogak, dailyJogak))
                .toList();
        return new OneTimeJogakListResult(results.size(), results);
    }

    @Override
    public DailyJogakListResult getDayJogaks(Long userId, LocalDate day) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if (day.isAfter(LocalDate.now())) {
            // 미래 루틴 조각 가져오기
            List<Jogak> userRoutineJogaks = jogakRepository.findDailyRoutineJogaks(user, dateToNum(day));
            List<DailyJogakResult> results = userRoutineJogaks.stream()
                    .filter(jogak -> jogak.getEndAt() == null || jogak.getEndAt().isAfter(day))
                    .map(DailyJogakResult::fromFutureRoutineJogak)
                    .toList();
            return new DailyJogakListResult(results.size(), results);
        }
        List<DailyJogakResult> results = dailyJogakRepository.findDailyJogaks(user, day).stream()
                .map(DailyJogakResult::from)
                .toList();
        return new DailyJogakListResult(results.size(), results);
    }

    /**
     * 주간/월간 루틴 가져오는 API
     * */
    @Override
    public List<RoutineJogakResult> getRoutineJogaks(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        List<LocalDate> pastDates = getPastDates(startDate, endDate);
        List<LocalDate> futureDates = getFutureDates(startDate, endDate);
        List<RoutineJogakResult> routineJogaks = new ArrayList<>();

        // 오늘 + 이전 가져오기
        if (!pastDates.isEmpty()) {
            List<DailyJogak> pastJogaks = dailyJogakRepository.findDailyJogaksBetween(user, startDate, endDate);
            routineJogaks.addAll(pastJogaks.stream()
                    .map(RoutineJogakResult::from)
                    .collect(Collectors.toList()));
        }

        // 미래 가져오기
        if (!futureDates.isEmpty()) {
            Map<Integer, List<Jogak>> dailyRoutineJogaks = new HashMap<>();
            // 월~금 루틴 조각 가져오기
            List<Jogak> userRoutineJogaks = jogakRepository.findAllRoutineJogaksByUser(userId);
            IntStream.rangeClosed(1, 7).forEach(i -> {
                List<Jogak> matchingJogaks = userRoutineJogaks.stream()
                        .filter(jogak -> jogak.getJogakPeriods().stream()
                                .anyMatch(jogakPeriod -> {
                                    Period period = jogakPeriod.getPeriod();
                                    return i == period.getId();
                                }))
                        .collect(Collectors.toList());
                dailyRoutineJogaks.put(i, matchingJogaks);
                log.debug("루틴 day: " + i + " " + dailyRoutineJogaks.get(i));
            });
            // 요일 값 대입
            for (LocalDate date: futureDates) {
                dailyRoutineJogaks.get(dateToNum(date))
                        .forEach(i -> {
                            log.debug(i.getEndAt() + " , " + date);
                            // 기간에 해당하지 않는 조각은 가져오지 않는 로직
                            if (i.getEndAt() == null || i.getEndAt().isAfter(date)) {
                                routineJogaks.add(RoutineJogakResult.fromFutureRoutineJogak(date, i.getTitle()));
                            }
                        });
            }
        }
        return routineJogaks;
    }

    private List<LocalDate> getPastDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        List<LocalDate> pastDates = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (date.isBefore(today)) {
                pastDates.add(date);
            }
        }
        return pastDates;
    }

    private List<LocalDate> getFutureDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        List<LocalDate> futureDates = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (date.isAfter(today)) {
                futureDates.add(date);
            }
        }
        return futureDates;
    }

    @Transactional
    @Override
    public JogakDailyResult startJogak(Long userId, Long jogakId) {
        Jogak jogak = jogakRepository.findActiveById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        validateJogakOwner(userId, jogak);

        LocalDate today = LocalDate.now();
        if (jogak.getIsRoutine() ||
                dailyJogakRepository.findActiveByJogakAndTargetDate(jogak, today).isPresent()) {
            throw new JogakException(ErrorCode.ALREADY_START_JOGAK);
        }
        DailyJogak dailyJogak = dailyJogakRepository.save(DailyJogak.create(jogak, today));
        return JogakDailyResult.from(jogak, dailyJogak);
    }

    @Transactional
    @Override
    public JogakDailyResult successJogak(Long userId, Long dailyJogakId) {
        DailyJogak dailyJogak = dailyJogakRepository.findActiveByIdWithJogakGraph(dailyJogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_DAILY_JOGAK));
        validateDailyJogakOwner(userId, dailyJogak);
        Jogak jogak = dailyJogak.getJogak();

        updateStatus(DailyJogakStatus.SUCCESS, jogak, dailyJogak);

        return JogakDailyResult.from(jogak, dailyJogak);
    }

    @Transactional
    @Override
    public JogakDailyResult failJogak(Long userId, Long dailyJogakId) {
        DailyJogak dailyJogak = dailyJogakRepository.findActiveByIdWithJogakGraph(dailyJogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_DAILY_JOGAK));
        validateDailyJogakOwner(userId, dailyJogak);
        Jogak jogak = dailyJogak.getJogak();

        updateStatus(DailyJogakStatus.FAIL, jogak, dailyJogak);

        return JogakDailyResult.from(jogak, dailyJogak);
    }

    private void updateStatus(DailyJogakStatus nextStatus, Jogak jogak, DailyJogak dailyJogak) {
        DailyJogakStatus currentStatus = dailyJogak.getStatus();
        if (currentStatus == nextStatus) {
            if (nextStatus == DailyJogakStatus.SUCCESS) {
                throw new BaseException(ErrorCode.ALREADY_SUCCESS_DAILY_JOGAK);
            }
            if (nextStatus == DailyJogakStatus.FAIL) {
                throw new BaseException(ErrorCode.ALREADY_FAIL_DAILY_JOGAK);
            }
            return;
        }
        if (nextStatus == DailyJogakStatus.SUCCESS) {
            jogak.increaseAchievements();
        }
        if (currentStatus == DailyJogakStatus.SUCCESS && nextStatus == DailyJogakStatus.FAIL) {
            jogak.decreaseAchievements();
        }
        dailyJogak.updateStatus(nextStatus);
    }

    private JogakDetailResult getJogakDetail(Jogak jogak) {
        Mogak mogak = mogakRepository.findByJogak(jogak)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_MOGAK));

        if (jogak.getIsRoutine()) {
            List<String> periods = periodRepository.findPeriodsByJogak(jogak).stream()
                    .map(Period::getDays)
                    .collect(Collectors.toList());
            return JogakDetailResult.from(jogak, mogak.getColor(), periods);
        }
        return JogakDetailResult.from(jogak, mogak.getColor());
    }

    @Override
    public JogakDetailResult getJogakDetail(Long userId, Long jogakId) {
        Jogak jogak = jogakRepository.findActiveById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        validateJogakOwner(userId, jogak);
        return getJogakDetail(jogak);
    }

    @Transactional
    @Override
    public void deleteJogakCascadeAfterParentAuthorization(Long jogakId) {
        Jogak jogak = jogakRepository.findActiveById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        jogakPeriodRepository.deleteAllByJogakId(jogakId);
        dailyJogakRepository.findActiveAllByJogak(jogak).forEach(dailyJogak -> {
            deletePostCascade(dailyJogak);
            dailyJogak.delete();
        });
        jogak.delete();
    }

    @Transactional
    @Override
    public void deleteJogak(Long userId, Long jogakId) {
        Jogak jogak = jogakRepository.findActiveById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        validateJogakOwner(userId, jogak);
        deleteJogakCascadeAfterParentAuthorization(jogakId);
    }

    private void validateMogakOwner(Long userId, Mogak mogak) {
        if (!Objects.equals(mogak.getUser().getId(), userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
    }

    private void deletePostCascade(DailyJogak dailyJogak) {
        postRepository.findActiveAllByDailyJogakId(dailyJogak.getId())
                .forEach(post -> {
                    postLikeRepository.deleteAllByPost(post);
                    postCommentRepository.findActiveAllByPostForCleanup(post).forEach(comment -> {
                        comment.delete();
                        post.subtractCommentCnt();
                    });
                    List<PostImg> postImgList = postImgRepository.findAllByPost(post);
                    if (!postImgList.isEmpty()) {
                        storageCleanupService.deletePostImagesAfterCommit(postImgList, DIR_NAME);
                        postImgRepository.deleteAllByPost(post);
                    }
                    post.delete();
                });
    }

    private void validateJogakOwner(Long userId, Jogak jogak) {
        if (!Objects.equals(jogak.getUser().getId(), userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
    }

    private void validateDailyJogakOwner(Long userId, DailyJogak dailyJogak) {
        if (!Objects.equals(dailyJogak.getJogak().getUser().getId(), userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
    }

    private int dateToNum(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue();
    }
}
