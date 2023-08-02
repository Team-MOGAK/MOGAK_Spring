package com.mogak.spring.service;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.MogakCategoryRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class MogakServiceImpl implements MogakService {
    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
    private final MogakCategoryRepository categoryRepository;

    @Transactional
    @Override
    public Mogak create(MogakRequestDto.CreateDto request) {
        System.out.println(request.getUserId());
        User user = userRepository.findById(request.getUserId())
                 .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));
        MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다"));
        State state = State.registerState(request.getStartAt(), LocalDate.now());
        return mogakRepository.save(MogakConverter.toMogak(request, category, user, state));
    }
}
