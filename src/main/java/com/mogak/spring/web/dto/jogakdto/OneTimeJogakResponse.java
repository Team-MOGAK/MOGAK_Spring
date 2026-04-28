package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record OneTimeJogakResponse(
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
    public static OneTimeJogakResponse from(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return new OneTimeJogakResponse(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                hasMatchingDailyJogak(jogak, dailyJogaks),
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }

    private static boolean hasMatchingDailyJogak(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return dailyJogaks.stream()
                .anyMatch(dailyJogak -> Objects.equals(dailyJogak.getJogak(), jogak));
    }
}
