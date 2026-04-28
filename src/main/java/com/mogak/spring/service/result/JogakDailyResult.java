package com.mogak.spring.service.result;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.Period;

import java.util.List;

public record JogakDailyResult(
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
    public static JogakDailyResult from(Jogak jogak, DailyJogak dailyJogak) {
        return new JogakDailyResult(
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
