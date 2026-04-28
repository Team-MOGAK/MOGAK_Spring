package com.mogak.spring.service.result;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record OneTimeJogakResult(
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
    public static OneTimeJogakResult from(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return new OneTimeJogakResult(
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
