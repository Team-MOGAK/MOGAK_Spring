package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class JogakConverter {
    public static Jogak toJogak(Mogak mogak, String title, Boolean isRoutine, LocalDate today, LocalDate endAt) {
        return Jogak.builder()
                .user(mogak.getUser())
                .mogak(mogak)
                .category(mogak.getBigCategory())
                .title(title)
                .isRoutine(isRoutine)
                .numberAchievements(0)
                .startAt(today)
                .endAt(endAt)
                .state(Validation.ACTIVE.toString())
                .build();
    }

    public static Jogak toJogak(DailyJogak dailyJogak) {
        return Jogak.builder()
                .mogak(dailyJogak.getMogak())
                .category(dailyJogak.getCategory())
                .title(dailyJogak.getTitle())
                .isRoutine(dailyJogak.getIsRoutine())
                .state(Validation.ACTIVE.toString())
                .build();
    }

    public static DailyJogak toDailyJogak(Jogak jogak) {
        return DailyJogak.builder()
                .mogak(jogak.getMogak())
                .category(jogak.getCategory())
                .title(jogak.getTitle())
                .achievement(false)
                .isRoutine(jogak.getIsRoutine())
                .build();
    }

    public static JogakResponseDto.GetJogakDto toGetJogakResponseDto(Jogak jogak) {
        return JogakResponseDto.GetJogakDto.builder()
                .jogakId(jogak.getId())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .startDate(jogak.getStartAt())
                .endDate(jogak.getEndAt())
                .build();
    }

    public static JogakResponseDto.GetDailyJogakDto toGetDailyJogakResponseDto(DailyJogak jogak) {
        return JogakResponseDto.GetDailyJogakDto.builder()
                .dailyJogakId(jogak.getId())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .build();
    }

    public static JogakResponseDto.GetJogakListDto toGetJogakListResponseDto(List<Jogak> jogaks) {
        return JogakResponseDto.GetJogakListDto.builder()
                .jogaks(jogaks.stream()
                        .map(JogakConverter::toGetJogakResponseDto)
                        .collect(Collectors.toList()))
                .size(jogaks.size())
                .build();
    }

    public static JogakResponseDto.GetDailyJogakListDto toGetDailyJogakListResponseDto(List<DailyJogak> jogaks) {
        return JogakResponseDto.GetDailyJogakListDto.builder()
                .dailyJogaks(jogaks.stream()
                        .map(JogakConverter::toGetDailyJogakResponseDto)
                        .collect(Collectors.toList()))
                .size(jogaks.size())
                .build();
    }

    public static JogakResponseDto.StartDailyJogakDto toStartJogakDto(DailyJogak dailyJogak) {
        return JogakResponseDto.StartDailyJogakDto.builder()
                .dailyJogakId(dailyJogak.getId())
                .title(dailyJogak.getTitle())
                .build();
    }

    public static JogakResponseDto.JogakSuccessDto toSuccessJogak(Jogak jogak) {
        return JogakResponseDto.JogakSuccessDto.builder()
                .title(jogak.getMogak().getTitle())
                .mogakTitle(jogak.getMogak().getTitle())
                .title(jogak.getCategory().getName())
                .build();
    }
}
