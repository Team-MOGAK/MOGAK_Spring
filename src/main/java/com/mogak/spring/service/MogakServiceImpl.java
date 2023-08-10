package com.mogak.spring.service;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakState;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.mogak.MogakPeriod;
import com.mogak.spring.domain.mogak.Period;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@AllArgsConstructor
@Service
public class MogakServiceImpl implements MogakService {
    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
    private final MogakPeriodRepository mogakPeriodRepository;
    private final PeriodRepository periodRepository;
    private final MogakCategoryRepository categoryRepository;

    private final JogakRepository jogakRepository;

    /**
     * 모각 생성
     * */
    @Transactional
    @Override
    public Mogak create(MogakRequestDto.CreateDto request) {
        User user = userRepository.findById(request.getUserId())
                 .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));
        String otherCategory = request.getOtherCategory();
        MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory())
                .orElseThrow(() -> new RuntimeException("카테고리가 존재하지 않습니다"));
        if (category.getName().equals("기타") && otherCategory == null) {
            throw new RuntimeException("기타 카테고리가 존재하지 않습니다");
        }
        State state = State.registerState(request.getStartAt(), request.getEndAt(), LocalDate.now());
        Mogak result = mogakRepository.save(MogakConverter.toMogak(request, category, otherCategory, user, state));
        saveMogakPeriod(request.getDays(), result);
        return result;
    }

    /**
     * 모각주기 저장 메소드
     * */
    private void saveMogakPeriod(List<String> days, Mogak mogak) {
        List<Period> periods = new ArrayList<>();
        for (String day: days) {
            periods.add(periodRepository.findOneByDays(day).orElseThrow(IllegalArgumentException::new));
        }
        for (Period period: periods) {
            MogakPeriod mogakPeriod = MogakPeriod.builder()
                    .period(period)
                    .mogak(mogak)
                    .build();
            mogakPeriodRepository.save(mogakPeriod);
        }
    }

    /**
     * 모각주기 업데이트 메소드
     * */
    private void updateMogakPeriod(List<String> days, Mogak mogak) {

        List<Period> periods = new ArrayList<>();
        for (String day : days) {
            periods.add(periodRepository.findOneByDays(day).orElseThrow(IllegalAccessError::new));
        }
        List<MogakPeriod> mogakPeriods = mogakPeriodRepository.findAllByMogak_Id(mogak.getId());

        int periodSize = periods.size();
        int mpSize = mogakPeriods.size();
        if (mpSize == periodSize) {
            IntStream.range(0, periodSize)
                    .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
        } else if (mpSize > periodSize) {
            IntStream.range(0, periodSize)
                    .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
            IntStream.range(periodSize, mpSize)
                    .forEach(i -> {
                        mogakPeriodRepository.delete(mogakPeriods.get(i));
                    });
        } else {
            IntStream.range(0, mpSize)
                    .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
            IntStream.range(mpSize, periodSize)
                    .forEach(i -> {
                        MogakPeriod mogakPeriod = MogakPeriod.builder()
                                .period(periods.get(i))
                                .mogak(mogak)
                                .build();
                        mogakPeriodRepository.save(mogakPeriod);
                    });
        }
    }

    /**
     * 모각 미리 달성 메소드
     * */
    @Transactional
    @Override
    public Mogak achieveMogak(Long id) {
        Mogak mogak = mogakRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        if (!mogak.getState().equals("ONGOING")) {
            throw new RuntimeException("잘못된 상태 변경입니다");
        }
        mogak.updateState(State.COMPLETE.toString());
        return mogak;
    }

    /**
     * 모각 업데이트 메소드
     * */
    @Transactional
    @Override
    public Mogak updateMogak(MogakRequestDto.UpdateDto request) {
        List<String> days = request.getDays();
        Mogak mogak = mogakRepository.findById(request.getMogakId())
                .orElseThrow(IllegalArgumentException::new);
        if (Optional.ofNullable(request.getCategory()).isPresent()) {
            MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));
            if (category.getName().equals("기타") && request.getOtherCategory() == null) {
                throw new IllegalArgumentException("기타 카테고리가 존재하지 않습니다");
            }
            if (category.getName().equals("기타")) {
                mogak.updateOtherCategory(request.getOtherCategory());
            }
            mogak.updateCategory(category);
        }

        mogak.updateFromDto(request);
        if (Optional.ofNullable(days).isPresent()) {
            updateMogakPeriod(days, mogak);
        }
        return mogak;
    }

    /**
     * 모각 조회(페이징)
     * */
    @Override
    public List<Mogak> getMogakList(Long userId, int cursor, int size) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        PageRequest pageRequest = PageRequest.of(cursor, size);
        return mogakRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageRequest);
    }

    /**
     * 진행중이고 해당 날의 모각들 불러오기
     * */
    @Override
    public List<Mogak> getOngoingTodayMogakList(int today) {
        return mogakRepository.findAllOngoingToday(State.ONGOING.name(), today);
    }

    /**
     * 모각 결과 내리기
     * 4시에 동작
     * 자정 전에 시작했던 조각들 때문
     * */
    @Transactional
    @Override
    public void judgeMogakByDay(LocalDate day) {
        List<Mogak> mogaks = mogakRepository.findAllByEndAt(day);
        for (Mogak mogak: mogaks) {
            List<Jogak> jogaks = jogakRepository.findAllByMogak(mogak);
            double achievementRate = getAcheiveRate(jogaks);
            judgeMogak(mogak, achievementRate);
        }
    }

    private double getAcheiveRate(List<Jogak> jogaks) {
        int success = 0;
        for (Jogak jogak: jogaks) {
            if (jogak.getState().equals(JogakState.SUCCESS.name())) {
                success += 1;
            }
        }
        return (double) success / jogaks.size() * 100;
    }

    private void judgeMogak(Mogak mogak, double rate) {
        if (rate >= 80.0) {
            mogak.updateState(State.COMPLETE.name());
        } else {
            mogak.updateState(State.FAIL.name());
        }
    }

    /**
     * 모각 삭제 API
     * */
    @Transactional
    @Override
    public void deleteMogak(Long mogakId) {
        //모각 존재 확인
        Mogak mogak = mogakRepository.findById(mogakId).orElseThrow(IllegalArgumentException::new);
        // 모각 주기 삭제
        mogakPeriodRepository.deleteAllByMogakId(mogakId);
        // 조각 삭제 필요
        jogakRepository.deleteAll(mogak.getJogaks());
        // 회고록 삭제 + 회고록 삭제에서 댓글 삭제도 같이 구현 필요

        mogakRepository.deleteById(mogakId);
    }

}
