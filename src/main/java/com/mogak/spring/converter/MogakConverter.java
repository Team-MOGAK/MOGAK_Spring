package com.mogak.spring.converter;

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
                .title(request.title())
                .color(request.color())
                .build();
    }

    /**
     * 여러 모각 조회
     * */
    public static MogakResponseDto.GetMogakListDto toGetMogakListDto(List<Mogak> mogaks) {
        return new MogakResponseDto.GetMogakListDto(
                mogaks.stream()
                        .map(MogakConverter::toGetMogakDto)
                        .collect(Collectors.toList()),
                mogaks.size()
        );
    }

    /**
     * 단일 모각 조회
     * */
    public static MogakResponseDto.GetMogakDto toGetMogakDto(Mogak mogak) {
        return new MogakResponseDto.GetMogakDto(
                mogak.getId(),
                mogak.getTitle(),
                mogak.getBigCategory(),
                mogak.getSmallCategory(),
                mogak.getColor()
        );
    }
}
