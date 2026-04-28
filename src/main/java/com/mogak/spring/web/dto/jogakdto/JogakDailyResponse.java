package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.Period;

import java.util.List;

public record JogakDailyResponse(
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
    public static JogakDailyResponse from(Jogak jogak, DailyJogak dailyJogak) {
        return new JogakDailyResponse(
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
