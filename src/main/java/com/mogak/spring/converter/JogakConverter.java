package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.JogakResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class JogakConverter {
    public static Jogak toJogak(Mogak mogak) {
        return Jogak.builder()
                .mogak(mogak)
                .build();
    }
//    public static JogakResponseDto.CreateJogakDto toCreateJogakResponseDto(Jogak jogak) {
//        return JogakResponseDto.CreateJogakDto.builder()
//                .startTime(jogak.getStartTime())
//                .build();
//    }

//    public static JogakResponseDto.GetJogakDto toGetJogakResponseDto(Jogak jogak) {
//        return JogakResponseDto.GetJogakDto.builder()
//                .mogakTitle(jogak.getMogak().getTitle())
//                .startTime(jogak.getStartTime())
//                .endTime(jogak.getEndTime())
//                .build();
//    }

//    public static JogakResponseDto.GetJogakListDto toGetJogakListResponseDto(List<Jogak> jogaks) {
//        return JogakResponseDto.GetJogakListDto.builder()
//                .jogaks(jogaks.stream()
//                        .map(JogakConverter::toGetJogakResponseDto)
//                        .collect(Collectors.toList()))
//                .size(jogaks.size())
//                .build();
//    }

//    public static JogakResponseDto.startJogakDto toGetStartJogakDto(Jogak jogak) {
//        return JogakResponseDto.startJogakDto.builder()
//                .title(jogak.getMogak().getTitle())
//                .startTime(jogak.getStartTime())
//                .build();
//    }

//    public static JogakResponseDto.endJogakDto toEndJogakDto(Jogak jogak) {
//        return JogakResponseDto.endJogakDto.builder()
//                .title(jogak.getMogak().getTitle())
//                .startTime(jogak.getStartTime())
//                .endTime(jogak.getEndTime())
//                .build();
//    }
}
