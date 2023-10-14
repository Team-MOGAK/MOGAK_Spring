package com.mogak.spring.converter;

import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.MogakRequestDto;
import com.mogak.spring.web.dto.MogakResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class MogakConverter {

    public static Mogak toMogak(MogakRequestDto.CreateDto request, MogakCategory mogakCategory, String smallCategory, User user, State state) {
        return Mogak.builder()
                .user(user)
                .bigCategory(mogakCategory)
                .smallCategory(smallCategory)
                .title(request.getTitle())
                .state(state.toString())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .validation(Validation.ACTIVE.toString())
                .build();
    }

    public static MogakResponseDto.CreateDto toCreateDto(Mogak mogak) {
        return MogakResponseDto.CreateDto.builder()
                .mogakId(mogak.getId())
                .title(mogak.getTitle())
                .build();
    }

    public static MogakResponseDto.UpdateStateDto toUpdateDto(Mogak mogak) {
        return MogakResponseDto.UpdateStateDto.builder()
                .mogakId(mogak.getId())
                .updatedAt(mogak.getUpdatedAt())
                .build();
    }

    /**
     * 여러 모각 조회
     * */
    public static MogakResponseDto.GetMogakListDto toGetMogakListDto(List<Mogak> mogaks) {
        return MogakResponseDto.GetMogakListDto.builder()
                .mogaks(mogaks.stream()
                        .map(MogakConverter::toGetMogakDto)
                        .collect(Collectors.toList()))
                .size(mogaks.size())
                .build();
    }

    /**
     * 단일 모각 조회
     * */
    public static MogakResponseDto.GetMogakDto toGetMogakDto(Mogak mogak) {
        return MogakResponseDto.GetMogakDto.builder()
                .title(mogak.getTitle())
                .state(mogak.getState())
                .periods(mogak.getPeriod())
                .mogakCategory(mogak.getBigCategory())
                .otherCategory(mogak.getSmallCategory())
                .startAt(mogak.getStartAt())
                .endAt(mogak.getEndAt())
                .build();
    }
}
