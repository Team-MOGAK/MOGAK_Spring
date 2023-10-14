package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.CommonException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import com.mogak.spring.web.dto.ModaratDto.ModaratResponseDto;

public class ModaratConverter {

    public static Modarat toModarat(User user, ModaratRequestDto.CreateModaratDto request) {
        if (request.getTitle() == null || request.getTitle().length() > 100) {
            throw new CommonException(ErrorCode.EXCEED_MAX_NUM_MODARAT);
        }
        return Modarat.builder()
                .user(user)
                .title(request.getTitle())
                .color(request.getColor())
                .validation(Validation.ACTIVE.toString())
                .build();
    }

    public static ModaratResponseDto.CreateModaratDto toCreateDto(Modarat modarat) {
        return ModaratResponseDto.CreateModaratDto.builder()
                .user(modarat.getUser())
                .title(modarat.getTitle())
                .color(modarat.getColor())
                .build();
    }

}
