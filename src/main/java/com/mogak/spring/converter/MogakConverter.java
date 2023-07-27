package com.mogak.spring.converter;

import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.MogakRequestDto;
import com.mogak.spring.web.dto.MogakResponseDto;

public class MogakConverter {

    public static Mogak toMogak(MogakRequestDto.CreateDto request, MogakCategory mogakCategory, User user, State state) {
        return Mogak.builder()
                .user(user)
                .category(mogakCategory)
                .title(request.getTitle())
                .state(state.toString())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .validation(Validation.ACTIVE.toString())
                .build();
    }
}
