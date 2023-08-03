package com.mogak.spring.service;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.mogak.MogakPeriod;
import com.mogak.spring.domain.mogak.Period;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class MogakServiceImpl implements MogakService {
    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
    private final MogakPeriodRepository mogakPeriodRepository;
    private final PeriodRepository periodRepository;
    private final MogakCategoryRepository categoryRepository;

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

        List<Period> periods = new ArrayList<>();
        List<String> requestDays = request.getDays();
        for (String day: requestDays) {
            periods.add(periodRepository.findOneByDays(day));
        }

        for (Period period: periods) {
            MogakPeriod mogakPeriod = MogakPeriod.builder()
                    .period(period)
                    .mogak(result)
                    .build();
            mogakPeriodRepository.save(mogakPeriod);
        }

        return result;
    }
}
