package com.mogak.spring.service.result;

import com.mogak.spring.domain.jogak.DailyJogak;

import java.time.LocalDate;

public record RoutineJogakResult(Long dailyJogakId, LocalDate date, Boolean isAchievement, String title) {
    public static RoutineJogakResult from(DailyJogak dailyJogak) {
        return new RoutineJogakResult(
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                dailyJogak.isSuccess(),
                dailyJogak.getTitle()
        );
    }

    public static RoutineJogakResult fromFutureRoutineJogak(LocalDate date, String title) {
        return new RoutineJogakResult(-1L, date, false, title);
    }
}
