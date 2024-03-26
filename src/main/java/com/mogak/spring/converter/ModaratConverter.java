package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;

import static com.mogak.spring.web.dto.modaratdto.ModaratResponseDto.ModaratDto;

public class ModaratConverter {
    public static Modarat toModarat(User user, ModaratRequestDto.CreateModaratDto request) {
        return Modarat.builder()
                .user(user)
                .title(request.getTitle())
                .color(request.getColor())
                .validation(Validation.ACTIVE.toString())
                .build();
    }

    public static ModaratDto toModaratDto(Modarat modarat) {
        return ModaratDto.builder()
                .id(modarat.getId())
                .title(modarat.getTitle())
                .color(modarat.getColor())
                .build();
    }

}
