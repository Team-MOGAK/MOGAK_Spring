package com.mogak.spring.web.dto.jogakdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.Period;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JogakResponseDto {

    public static CreateJogakDto createFromJogak(Jogak jogak) {
        return createFromJogak(jogak, null);
    }

    public static CreateJogakDto createFromJogak(Jogak jogak, List<String> days) {
        return CreateJogakDto.builder()
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

    public static DetailJogakDto detailFromJogak(Jogak jogak, String color) {
        return detailFromJogak(jogak, color, null);
    }

    public static DetailJogakDto detailFromJogak(Jogak jogak, String color, List<String> days) {
        return DetailJogakDto.builder()
                .jogakId(jogak.getId())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .days(days)
                .color(color)
                .achievements(jogak.getAchievements())
                .startDate(jogak.getStartAt())
                .endDate(jogak.getEndAt())
                .build();
    }

    public static GetJogakDto getJogakFrom(Jogak jogak, Boolean isAlreadyAdded) {
        return GetJogakDto.builder()
                .jogakId(jogak.getId())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .days(jogak.getPeriods())
                .isAlreadyAdded(isAlreadyAdded)
                .achievements(jogak.getAchievements())
                .startDate(jogak.getStartAt())
                .endDate(jogak.getEndAt())
                .build();
    }

    public static GetDailyJogakDto fromDailyJogak(DailyJogak dailyJogak) {
        return GetDailyJogakDto.builder()
                .jogakId(dailyJogak.getJogak().getId())
                .dailyJogakId(dailyJogak.getId())
                .mogakTitle(dailyJogak.getMogak().getTitle())
                .category(dailyJogak.getCategory().getName())
                .title(dailyJogak.getTitle())
                .isRoutine(dailyJogak.getIsRoutine())
                .isAchievement(dailyJogak.isSuccess())
                .build();
    }

    public static GetDailyJogakDto futureDailyJogakFromJogak(Jogak jogak) {
        return GetDailyJogakDto.builder()
                .dailyJogakId(-1L)
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .title(jogak.getTitle())
                .isRoutine(jogak.getIsRoutine())
                .isAchievement(false)
                .build();
    }

    public static GetDailyJogakListDto dailyJogakListFrom(List<DailyJogak> dailyJogaks) {
        return GetDailyJogakListDto.builder()
                .dailyJogaks(dailyJogaks.stream()
                        .map(JogakResponseDto::fromDailyJogak)
                        .collect(Collectors.toList()))
                .size(dailyJogaks.size())
                .build();
    }

    public static GetDailyJogakListDto dailyJogakListFromDtos(List<GetDailyJogakDto> dailyJogaks) {
        return GetDailyJogakListDto.builder()
                .dailyJogaks(dailyJogaks)
                .size(dailyJogaks.size())
                .build();
    }

    public static GetOneTimeJogakListDto oneTimeJogakListFrom(List<Jogak> jogaks, List<DailyJogak> dailyJogaks) {
        return GetOneTimeJogakListDto.builder()
                .jogaks(jogaks.stream()
                        .map(jogak -> GetOneTimeJogakDto.builder()
                                .jogakId(jogak.getId())
                                .mogakTitle(jogak.getMogak().getTitle())
                                .category(jogak.getCategory().getName())
                                .title(jogak.getTitle())
                                .isRoutine(jogak.getIsRoutine())
                                .isAlreadyAdded(hasMatchingDailyJogak(jogak, dailyJogaks))
                                .achievements(jogak.getAchievements())
                                .startDate(jogak.getStartAt())
                                .endDate(jogak.getEndAt())
                                .build())
                        .collect(Collectors.toList()))
                .size(jogaks.size())
                .build();
    }

    public static GetRoutineJogakDto routineJogakFrom(DailyJogak dailyJogak) {
        return GetRoutineJogakDto.builder()
                .dailyJogakId(dailyJogak.getId())
                .date(dailyJogak.getTargetDate())
                .isAchievement(dailyJogak.isSuccess())
                .title(dailyJogak.getTitle())
                .build();
    }

    public static GetRoutineJogakDto futureRoutineJogakFrom(LocalDate date, String title) {
        return GetRoutineJogakDto.builder()
                .dailyJogakId(-1L)
                .date(date)
                .isAchievement(false)
                .title(title)
                .build();
    }

    public static JogakDailyJogakDto jogakDailyJogakFrom(Jogak jogak, DailyJogak dailyJogak) {
        return JogakDailyJogakDto.builder()
                .jogakId(jogak.getId())
                .dailyJogakId(dailyJogak.getId())
                .title(dailyJogak.getTitle())
                .mogakTitle(jogak.getMogak().getTitle())
                .category(jogak.getCategory().getName())
                .isRoutine(jogak.getIsRoutine())
                .isAchievement(dailyJogak.isSuccess())
                .achievements(jogak.getAchievements())
                .build();
    }

    private static boolean hasMatchingDailyJogak(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return dailyJogaks.stream()
                .anyMatch(dailyJogak -> Objects.equals(dailyJogak.getJogak(), jogak));
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateJogakDto {
        private Long jogakId;
        private String mogakTitle;
        private String category;
        private String title;
        private Boolean isRoutine;
        private List<String> days;
        private Integer achievements;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DetailJogakDto {
        private Long jogakId;
        private String mogakTitle;
        private String category;
        private String title;
        private Boolean isRoutine;
        private List<String> days;
        private String color;
        private Integer achievements;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakDto {
        private Long jogakId;
        private String mogakTitle;
        private String category;
        private String title;
        private Boolean isRoutine;
        private List<String> days;
        private Boolean isAlreadyAdded;
        private Integer achievements;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetDailyJogakDto {
        private Long jogakId;
        private Long dailyJogakId;
        private String mogakTitle;
        private String category;
        private String title;
        private Boolean isRoutine;
        private Boolean isAchievement;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetOneTimeJogakDto {
        private Long jogakId;
        private String mogakTitle;
        private String category;
        private String title;
        private Boolean isRoutine;
        private Boolean isAlreadyAdded;
        private Integer achievements;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakListDto {
        private int size;
        private List<JogakResponseDto.GetJogakDto> jogaks;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetDailyJogakListDto {
        private int size;
        private List<JogakResponseDto.GetDailyJogakDto> dailyJogaks;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetOneTimeJogakListDto {
        private int size;
        private List<JogakResponseDto.GetOneTimeJogakDto> jogaks;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetRoutineJogakDto {
        private Long dailyJogakId;
        private LocalDate date;
        private Boolean isAchievement;
        private String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JogakSuccessDto {
        private String title;
        private String mogakTitle;
        private String category;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JogakDailyJogakDto {
        private Long jogakId;
        private Long dailyJogakId;
        private String title;
        private String mogakTitle;
        private String category;
        private Boolean isRoutine;
        private List<Period> days;
        private Boolean isAchievement;
        private Integer achievements;
    }
}
