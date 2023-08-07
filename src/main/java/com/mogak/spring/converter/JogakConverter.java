package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.JogakResponseDto;

public class JogakConverter {
    public static Jogak toJogak(Mogak mogak) {
        return Jogak.builder()
                .mogak(mogak)
                .build();
    }
    public static JogakResponseDto.JogakDto toJogakResponseDto(Jogak jogak) {
        return JogakResponseDto.JogakDto.builder()
                .startTime(jogak.getStartTime())
                .build();
    }

}
