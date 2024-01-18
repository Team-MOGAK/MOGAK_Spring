package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class MogakConverter {

    public static Mogak toMogak(MogakRequestDto.CreateDto request, Modarat modarat, MogakCategory mogakCategory, String smallCategory, User user) {
        return Mogak.builder()
                .modarat(modarat)
                .user(user)
                .bigCategory(mogakCategory)
                .smallCategory(smallCategory)
                .title(request.getTitle())
                .color(request.getColor())
                .validation(Validation.ACTIVE.toString())
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
                .id(mogak.getId())
                .title(mogak.getTitle())
                .bigCategory(mogak.getBigCategory())
                .smallCategory(mogak.getSmallCategory())
                .color(mogak.getColor())
                .build();
    }
}
