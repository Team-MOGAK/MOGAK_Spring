package com.mogak.spring.web.dto.jogakdto;

import com.mogak.spring.domain.jogak.Period;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class JogakResponseDto {

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
        private Integer achievements;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetDailyJogakDto {
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
