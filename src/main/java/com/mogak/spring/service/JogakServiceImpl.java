package com.mogak.spring.service;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.converter.JogakPeriodConverter;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.JogakState;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.JogakException;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.JogakRequestDto;
import com.mogak.spring.web.dto.JogakResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JogakServiceImpl implements JogakService {

    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;
    private final JogakPeriodRepository jogakPeriodRepository;
    private final PeriodRepository periodRepository;

    /**
     * 자정에 Ongoing인 모든 모각 생성
     */
    @Transactional
    public void createJogakToday() {
//        List<Mogak> mogaks = mogakService.getOngoingTodayMogakList(Weeks.getTodayNum());
//        for (Mogak mogak : mogaks) {
//            createJogak(mogak.getId());
//        }
    }

    /**
     * 자정 1분까지 시작하지 않은 조각 실패 처리
     * +) 자정엔 조각 생성 스케줄이 있어서 1분 이후에 처리
     */
    @Transactional
    public void failJogakAtMidnight() {
        List<Jogak> jogaks = jogakRepository.findJogakByState(null);
        for (Jogak jogak : jogaks) {
            jogak.updateState(JogakState.FAIL);
        }
    }

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
    public JogakResponseDto.CreateJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto) {
        Mogak mogak = mogakRepository.findById(createJogakDto.getMogakId())
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        Jogak jogak = jogakRepository.save(JogakConverter.toJogak(mogak, mogak.getBigCategory(),
                createJogakDto.getTitle(), createJogakDto.getIsRoutine(), createJogakDto.getEndDate()));
        validatePeriod(Optional.ofNullable(createJogakDto.getIsRoutine()), Optional.ofNullable(createJogakDto.getDays()));

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
        return JogakConverter.toCreateJogakResponseDto(jogak);
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
                throw new JogakException(ErrorCode.NOT_VALID_UPDATE_JOGAK);
            }
        });
        daysOptional.ifPresent(days -> {
            if (isRoutineOptional.isEmpty()) {
                throw new JogakException(ErrorCode.NOT_VALID_UPDATE_JOGAK);
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
                        .filter(jogak -> !jogak.getIsRoutine())
                        )
            .collect(Collectors.toList());
        return JogakConverter.toGetJogakListResponseDto(jogakList);
    }

//    @Transactional
//    @Override
//    public Jogak startJogak(Long jogakId) {
//        Jogak jogak = jogakRepository.findById(jogakId).orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
//        jogak.start(LocalDateTime.now());
//        return jogak;
//    }

//    @Transactional
//    @Override
//    public Jogak endJogak(Long jogakId) {
//        Jogak jogak = jogakRepository.findById(jogakId).orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
//        jogak.end(LocalDateTime.now());
//        return jogak;
//    }

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
