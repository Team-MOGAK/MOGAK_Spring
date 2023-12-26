package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class JogakConverter {
    public static Jogak toInitialJogak(Mogak mogak, String title, Boolean isRoutine, LocalDate today, LocalDate endAt) {
        return Jogak.builder()
                .user(mogak.getUser())
                .mogak(mogak)
                .category(mogak.getBigCategory())
                .title(title)
                .isRoutine(isRoutine)
                .achievements(0)
                .startAt(today)
                .endAt(endAt)
                .build();
    }

    public static Jogak toJogak(DailyJogak dailyJogak) {
        return Jogak.builder()
                .mogak(dailyJogak.getMogak())
                .category(dailyJogak.getCategory())
                .title(dailyJogak.getTitle())
                .isRoutine(dailyJogak.getIsRoutine())
                .build();
    }

    public static DailyJogak toInitialDailyJogak(Jogak jogak) {
        return DailyJogak.builder()
                .mogak(jogak.getMogak())
                .category(jogak.getCategory())
                .title(jogak.getTitle())
                .isAchievement(false)
                .jogakId(jogak.getId())
                .isRoutine(jogak.getIsRoutine())
                .build();
    }

    public static JogakResponseDto.GetJogakDto toGetJogakResponseDto(Jogak jogak, List<String> days) {
        return JogakResponseDto.GetJogakDto.builder()
                .jogakId(jogak.getId())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .days(days)
                .achievements(jogak.getAchievements())
                .startDate(jogak.getStartAt())
                .endDate(jogak.getEndAt())
                .build();
    }

    public static JogakResponseDto.GetJogakDto toGetJogakResponseDto(Jogak jogak) {
        return JogakResponseDto.GetJogakDto.builder()
                .jogakId(jogak.getId())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .days(jogak.getPeriods())
                .achievements(jogak.getAchievements())
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

    public static JogakResponseDto.JogakSuccessDto toSuccessJogak(Jogak jogak) {
        return JogakResponseDto.JogakSuccessDto.builder()
                .title(jogak.getMogak().getTitle())
                .mogakTitle(jogak.getMogak().getTitle())
                .title(jogak.getCategory().getName())
                .build();
    }

    public static JogakResponseDto.JogakDailyJogakDto toJogakDailyJogakDto(Jogak jogak, DailyJogak dailyJogak) {
        return JogakResponseDto.JogakDailyJogakDto.builder()
                .jogakId(jogak.getId())
                .dailyJogakId(dailyJogak.getId())
                .title(dailyJogak.getTitle())
                .mogakTitle(jogak.getTitle())
                .category(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .isAchievement(dailyJogak.getIsAchievement())
                .achievements(jogak.getAchievements())
                .build();
    }
}
