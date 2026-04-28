package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.Period;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JogakResponseDto {

    public static CreateJogakDto createFromJogak(Jogak jogak) {
        return createFromJogak(jogak, null);
    }

    public static CreateJogakDto createFromJogak(Jogak jogak, List<String> days) {
        return CreateJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                days,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static DetailJogakDto detailFromJogak(Jogak jogak, String color) {
        return detailFromJogak(jogak, color, null);
    }

    public static DetailJogakDto detailFromJogak(Jogak jogak, String color, List<String> days) {
        return DetailJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                days,
                color,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static GetJogakDto getJogakFrom(Jogak jogak, Boolean isAlreadyAdded) {
        return GetJogakDto.of(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                jogak.getPeriods(),
                isAlreadyAdded,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    public static GetDailyJogakDto fromDailyJogak(DailyJogak dailyJogak) {
        return GetDailyJogakDto.of(
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getMogak().getTitle(),
                dailyJogak.getCategory().getName(),
                dailyJogak.getTitle(),
                dailyJogak.getIsRoutine(),
                dailyJogak.isSuccess()
        );
    }

    public static GetDailyJogakDto futureDailyJogakFromJogak(Jogak jogak) {
        return GetDailyJogakDto.of(
                null,
                -1L,
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                false
        );
    }

    public static GetDailyJogakListDto dailyJogakListFrom(List<DailyJogak> dailyJogaks) {
        List<GetDailyJogakDto> dailyJogakDtos = dailyJogaks.stream()
                .map(JogakResponseDto::fromDailyJogak)
                .collect(Collectors.toList());
        return new GetDailyJogakListDto(dailyJogakDtos.size(), dailyJogakDtos);
    }

    public static GetDailyJogakListDto dailyJogakListFromDtos(List<GetDailyJogakDto> dailyJogaks) {
        return new GetDailyJogakListDto(dailyJogaks.size(), dailyJogaks);
    }

    public static GetOneTimeJogakListDto oneTimeJogakListFrom(List<Jogak> jogaks, List<DailyJogak> dailyJogaks) {
        List<GetOneTimeJogakDto> jogakDtos = jogaks.stream()
                .map(jogak -> GetOneTimeJogakDto.of(
                        jogak.getId(),
                        jogak.getMogak().getTitle(),
                        jogak.getCategory().getName(),
                        jogak.getTitle(),
                        jogak.getIsRoutine(),
                        hasMatchingDailyJogak(jogak, dailyJogaks),
                        jogak.getAchievements(),
                        jogak.getStartAt(),
                        jogak.getEndAt()
                ))
                .collect(Collectors.toList());
        return new GetOneTimeJogakListDto(jogakDtos.size(), jogakDtos);
    }

    public static GetRoutineJogakDto routineJogakFrom(DailyJogak dailyJogak) {
        return GetRoutineJogakDto.of(
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                dailyJogak.isSuccess(),
                dailyJogak.getTitle()
        );
    }

    public static GetRoutineJogakDto futureRoutineJogakFrom(LocalDate date, String title) {
        return GetRoutineJogakDto.of(-1L, date, false, title);
    }

    public static JogakDailyJogakDto jogakDailyJogakFrom(Jogak jogak, DailyJogak dailyJogak) {
        return JogakDailyJogakDto.of(
                jogak.getId(),
                dailyJogak.getId(),
                dailyJogak.getTitle(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getIsRoutine(),
                null,
                dailyJogak.isSuccess(),
                jogak.getAchievements()
        );
    }

    private static boolean hasMatchingDailyJogak(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return dailyJogaks.stream()
                .anyMatch(dailyJogak -> Objects.equals(dailyJogak.getJogak(), jogak));
    }

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
