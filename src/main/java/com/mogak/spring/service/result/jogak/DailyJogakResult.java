package com.mogak.spring.service.result.jogak;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

public record DailyJogakResult(
        Long jogakId,
        Long dailyJogakId,
        String mogakTitle,
        String category,
        String title,
        Boolean isRoutine,
        Boolean isAchievement
) {
    public static DailyJogakResult from(DailyJogak dailyJogak) {
        return new DailyJogakResult(
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getMogak().getTitle(),
                dailyJogak.getCategory().getName(),
                dailyJogak.getTitle(),
                dailyJogak.getIsRoutine(),
                dailyJogak.isSuccess()
        );
    }

    public static DailyJogakResult futureFrom(Jogak jogak) {
        return new DailyJogakResult(
                null,
                -1L,
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                false
        );
    }
}
