package com.mogak.spring.converter;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;

import static com.mogak.spring.web.dto.modaratdto.ModaratResponseDto.ModaratDto;

public class ModaratConverter {
    public static Modarat toModarat(User user, ModaratRequestDto.CreateModaratDto request) {
        return Modarat.builder()
                .user(user)
                .title(request.title())
                .color(request.color())
                .build();
    }

    public static ModaratDto toModaratDto(Modarat modarat) {
        return new ModaratDto(modarat.getId(), modarat.getTitle(), modarat.getColor());
    }

}
