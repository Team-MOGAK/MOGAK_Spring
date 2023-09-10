package com.mogak.spring.service;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakState;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.JogakException;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JogakServiceImpl implements JogakService {

    private final UserRepository userRepository;
    private final MogakService mogakService;
    private final MogakRepository mogakRepository;
    private final JogakRepository jogakRepository;

    /**
     * 자정에 Ongoing인 모든 모각 생성
     */
    @Transactional
    public void createJogakToday() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayNum = dayOfWeek.getValue();

        List<Mogak> mogaks = mogakService.getOngoingTodayMogakList(dayNum);
        for (Mogak mogak : mogaks) {
            createJogak(mogak.getId());
        }
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
    @Transactional
    public void failJogakAtFour() {
        List<Jogak> jogaks = jogakRepository.findJogakIsOngoingYesterday(JogakState.ONGOING.name());
        for (Jogak jogak : jogaks) {
            jogak.updateState(JogakState.FAIL);
        }
    }

    @Override
    public Jogak createJogak(Long mogakId) {
        Mogak mogak = mogakRepository.findById(mogakId).orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        if (!mogak.getState().equals(State.ONGOING.name())) {
            throw new JogakException(ErrorCode.WRONG_CREATE_JOGAK);
        }
        return jogakRepository.save(JogakConverter.toJogak(mogak));
    }

    @Override
    public List<Jogak> getDailyJogaks(Long userId) {
//        Long userId = JwtArgumentResolver.extractToken(req).orElseThrow(() -> new CommonException(ErrorCode.EMPTY_TOKEN));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        return jogakRepository.findDailyJogak(user);
    }

    @Transactional
    @Override
    public Jogak startJogak(Long jogakId) {
        Jogak jogak = jogakRepository.findById(jogakId).orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        jogak.start(LocalDateTime.now());
        return jogak;
    }

    @Transactional
    @Override
    public Jogak endJogak(Long jogakId) {
        Jogak jogak = jogakRepository.findById(jogakId).orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        jogak.end(LocalDateTime.now());
        return jogak;
    }

    @Override
    public void deleteJogak(Long jogakId) {
        Jogak jogak = jogakRepository.findById(jogakId).orElseThrow(() -> new JogakException(ErrorCode.NOT_EXIST_JOGAK));
        jogakRepository.deleteById(jogakId);
    }

}
