package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;

public class JogakConverter {
    public static Jogak toJogak(Mogak mogak) {
        return Jogak.builder()
                .mogak(mogak)
                .build();
    }
//    public static JogakResponseDto.JogakDto toJogakResponseDto(Jogak jogak) {
//        return JogakResponseDto.JogakDto.builder()
//                .startTime(jogak.getStartTime())
//                .build();
//    }

}
