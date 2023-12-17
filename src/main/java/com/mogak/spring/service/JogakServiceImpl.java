package com.mogak.spring.service;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.converter.JogakPeriodConverter;
import com.mogak.spring.domain.common.Weeks;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.JogakException;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
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

    /**
     * 자정에 Ongoing인 모든 모각 생성
     */
    @Transactional
    public void createRoutineJogakToday() {
        for (User user: userRepository.findAll()) {
            List<Jogak> jogaks  = jogakRepository.findDailyRoutineJogaks(user, Weeks.getTodayNum());
            for (Jogak jogak : jogaks) {
                dailyJogakRepository.save(JogakConverter.toDailyJogak(jogak));
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
    public JogakResponseDto.GetJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto) {
        Mogak mogak = mogakRepository.findById(createJogakDto.getMogakId())
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        Jogak jogak = jogakRepository.save(JogakConverter.toJogak(mogak, mogak.getBigCategory(),
                createJogakDto.getTitle(), createJogakDto.getIsRoutine(), createJogakDto.getEndDate()));
        validatePeriod(Optional.ofNullable(createJogakDto.getIsRoutine()), Optional.ofNullable(createJogakDto.getDays()));
        if (createJogakDto.getIsRoutine()) {
            List<Period> periods = new ArrayList<>();
            List<String> requestDays = createJogakDto.getDays();

            // 반복주기 추출
            for (String day: requestDays) {
                periods.add(periodRepository.findOneByDays(day)
                        .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_DAY)));
            }
            // 다대다-조각주기 저장
            for (Period period: periods) {
                jogakPeriodRepository.save(
                        JogakPeriod.builder()
                                .period(period)
                                .jogak(jogak)
                                .build()
                );
            }
        }
        return JogakConverter.toGetJogakResponseDto(jogak);
    }

    @Transactional
    @Override
    public void updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto) {
        Jogak jogak = jogakRepository.findById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        validatePeriod(Optional.ofNullable(updateJogakDto.getIsRoutine()), Optional.ofNullable(updateJogakDto.getDays()));
        jogak.update(updateJogakDto.getTitle(), updateJogakDto.getIsRoutine(), updateJogakDto.getEndDate());
        updateJogakPeriod(jogak, updateJogakDto.getDays());
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
        for (String day : days) {
            periods.add(periodRepository.findOneByDays(day)
                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_DAY)));
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
                    .forEach(i -> jogakPeriodRepository.save(JogakPeriodConverter.toJogakPeriod(periods.get(i), jogak)));
        }
    }

    @Override
    public JogakResponseDto.GetJogakListDto getDailyJogaks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        List<Jogak> jogakList = mogakRepository.findAllByUser(user).stream()
                .flatMap(mogak -> mogak.getJogaks().stream()
                        .filter(jogak -> !jogak.getIsRoutine()))
                .collect(Collectors.toList());
        return JogakConverter.toGetJogakListResponseDto(jogakList);
    }

    @Override
    public JogakResponseDto.GetDailyJogakListDto getTodayJogaks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return JogakConverter.toGetDailyJogakListResponseDto(jogakRepository.findDailyJogaks(
                user, Weeks.getTodayMidnight(), Weeks.getTodayMidnight().plusDays(1)));
    }

    /**
     * 주간/월간 루틴 가져오는 API
     * */
    @Override
    public List<JogakResponseDto.GetRoutineJogakDto> getRoutineJogaks(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        List<LocalDate> pastDates = getPastDates(startDate, endDate);
        List<LocalDate> futureDates = getFutureDates(startDate, endDate);
        List<JogakResponseDto.GetRoutineJogakDto> routineJogaks = new ArrayList<>();

        // 오늘 + 이전 가져오기
        if (!pastDates.isEmpty()) {
            List<DailyJogak> pastJogaks = dailyJogakRepository.findByDateRange(startDate.atStartOfDay(), endDate.atStartOfDay());
            routineJogaks.addAll(pastJogaks.stream()
                    .map(DailyJogak::getRoutineJogakDto)
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
                log.info("루틴 day: " + i + " " + dailyRoutineJogaks.get(i));
            });
            // 요일 값 대입
            for (LocalDate date: futureDates) {
                dailyRoutineJogaks.get(dateToNum(date))
                        .forEach(i -> {
                            log.info(i.getEndAt() + " , " + date);
                            // 기간에 해당하지 않는 조각은 가져오지 않는 로직
                            if (i.getEndAt().isAfter(date)) {
                                routineJogaks.add(DailyJogak.getFutureRoutineJogakDto(date, i.getTitle()));
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

    private int dateToNum(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    private int getTodayNum() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    @Transactional
    @Override
    public JogakResponseDto.StartDailyJogakDto startJogak(Long jogakId) {
        Jogak jogak = jogakRepository.findById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        if (jogak.getIsRoutine()) {
            throw new JogakException(ErrorCode.ALREADY_START_JOGAK);
        }
        return JogakConverter.toStartJogakDto((dailyJogakRepository.save(JogakConverter.toDailyJogak(jogak))));
    }

    @Transactional
    @Override
    public JogakResponseDto.JogakSuccessDto successJogak(Long dailyJogakId) {
        DailyJogak dailyjogak = dailyJogakRepository.findById(dailyJogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        dailyjogak.updateSuccess();
        return JogakConverter.toSuccessJogak(JogakConverter.toJogak(dailyjogak));
    }

    @Transactional
    @Override
    public void deleteJogak(Long jogakId) {
        Jogak jogak = jogakRepository.findById(jogakId)
                .orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        jogakPeriodRepository.deleteAllByJogakId(jogakId);
        // TODO: 변경된 코드에 맞춘 회고록 + 댓글 삭제
        jogakRepository.deleteById(jogakId);
    }
}
