package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.Period;

import java.time.LocalDate;
import java.util.List;

public class JogakResponseDto {

    public record CreateJogakDto(
            Long jogakId,
            String mogakTitle,
            String category,
            String title,
            @JsonProperty("isRoutine") Boolean isRoutine,
            List<String> days,
            Integer achievements,
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static CreateJogakDto of(
                Long jogakId,
                String mogakTitle,
                String category,
                String title,
                Boolean isRoutine,
                List<String> days,
                Integer achievements,
                LocalDate startDate,
                LocalDate endDate
        ) {
            return new CreateJogakDto(jogakId, mogakTitle, category, title, isRoutine, days, achievements, startDate, endDate);
        }
    }

    public record DetailJogakDto(
            Long jogakId,
            String mogakTitle,
            String category,
            String title,
            @JsonProperty("isRoutine") Boolean isRoutine,
            List<String> days,
            String color,
            Integer achievements,
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static DetailJogakDto of(
                Long jogakId,
                String mogakTitle,
                String category,
                String title,
                Boolean isRoutine,
                List<String> days,
                String color,
                Integer achievements,
                LocalDate startDate,
                LocalDate endDate
        ) {
            return new DetailJogakDto(jogakId, mogakTitle, category, title, isRoutine, days, color, achievements, startDate, endDate);
        }
    }

    public record GetJogakDto(
            Long jogakId,
            String mogakTitle,
            String category,
            String title,
            @JsonProperty("isRoutine") Boolean isRoutine,
            List<String> days,
            @JsonProperty("isAlreadyAdded") Boolean isAlreadyAdded,
            Integer achievements,
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static GetJogakDto of(
                Long jogakId,
                String mogakTitle,
                String category,
                String title,
                Boolean isRoutine,
                List<String> days,
                Boolean isAlreadyAdded,
                Integer achievements,
                LocalDate startDate,
                LocalDate endDate
        ) {
            return new GetJogakDto(jogakId, mogakTitle, category, title, isRoutine, days, isAlreadyAdded, achievements, startDate, endDate);
        }
    }

    public record GetDailyJogakDto(
            Long jogakId,
            Long dailyJogakId,
            String mogakTitle,
            String category,
            String title,
            @JsonProperty("isRoutine") Boolean isRoutine,
            @JsonProperty("isAchievement") Boolean isAchievement
    ) {
        public static GetDailyJogakDto of(
                Long jogakId,
                Long dailyJogakId,
                String mogakTitle,
                String category,
                String title,
                Boolean isRoutine,
                Boolean isAchievement
        ) {
            return new GetDailyJogakDto(jogakId, dailyJogakId, mogakTitle, category, title, isRoutine, isAchievement);
        }
    }

    public record GetOneTimeJogakDto(
            Long jogakId,
            String mogakTitle,
            String category,
            String title,
            @JsonProperty("isRoutine") Boolean isRoutine,
            @JsonProperty("isAlreadyAdded") Boolean isAlreadyAdded,
            Integer achievements,
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static GetOneTimeJogakDto of(
                Long jogakId,
                String mogakTitle,
                String category,
                String title,
                Boolean isRoutine,
                Boolean isAlreadyAdded,
                Integer achievements,
                LocalDate startDate,
                LocalDate endDate
        ) {
            return new GetOneTimeJogakDto(jogakId, mogakTitle, category, title, isRoutine, isAlreadyAdded, achievements, startDate, endDate);
        }
    }

    public record GetJogakListDto(int size, List<JogakResponseDto.GetJogakDto> jogaks) {
    }

    public record GetDailyJogakListDto(int size, List<JogakResponseDto.GetDailyJogakDto> dailyJogaks) {
    }

    public record GetOneTimeJogakListDto(int size, List<JogakResponseDto.GetOneTimeJogakDto> jogaks) {
    }

    public record GetRoutineJogakDto(
            Long dailyJogakId,
            LocalDate date,
            @JsonProperty("isAchievement") Boolean isAchievement,
            String title
    ) {
        public static GetRoutineJogakDto of(Long dailyJogakId, LocalDate date, Boolean isAchievement, String title) {
            return new GetRoutineJogakDto(dailyJogakId, date, isAchievement, title);
        }
    }

    public record JogakSuccessDto(String title, String mogakTitle, String category) {
    }

    public record JogakDailyJogakDto(
            Long jogakId,
            Long dailyJogakId,
            String title,
            String mogakTitle,
            String category,
            @JsonProperty("isRoutine") Boolean isRoutine,
            List<Period> days,
            @JsonProperty("isAchievement") Boolean isAchievement,
            Integer achievements
    ) {
        public static JogakDailyJogakDto of(
                Long jogakId,
                Long dailyJogakId,
                String title,
                String mogakTitle,
                String category,
                Boolean isRoutine,
                List<Period> days,
                Boolean isAchievement,
                Integer achievements
        ) {
            return new JogakDailyJogakDto(jogakId, dailyJogakId, title, mogakTitle, category, isRoutine, days, isAchievement, achievements);
        }
    }
}
