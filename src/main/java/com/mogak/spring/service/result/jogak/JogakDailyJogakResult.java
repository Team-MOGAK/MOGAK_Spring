package com.mogak.spring.service.result.jogak;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.Period;

import java.util.List;

public record JogakDailyJogakResult(
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
    public static JogakDailyJogakResult of(Jogak jogak, DailyJogak dailyJogak) {
        return new JogakDailyJogakResult(
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
}
