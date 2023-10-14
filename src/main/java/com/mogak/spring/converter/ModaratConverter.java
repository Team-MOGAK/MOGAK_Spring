package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import com.mogak.spring.web.dto.ModaratDto.ModaratResponseDto;

public class ModaratConverter {
    public static Modarat toModarat(User user, ModaratRequestDto.CreateModaratDto request) {
        return Modarat.builder()
                .user(user)
                .title(request.getTitle())
                .color(request.getColor())
                .validation(Validation.ACTIVE.toString())
                .build();
    }

    public static ModaratResponseDto.CreateModaratDto toCreateDto(Modarat modarat) {
        return ModaratResponseDto.CreateModaratDto.builder()
                .title(modarat.getTitle())
                .color(modarat.getColor())
                .build();
    }

}
