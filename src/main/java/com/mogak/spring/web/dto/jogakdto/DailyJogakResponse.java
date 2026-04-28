package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

public record DailyJogakResponse(
        Long jogakId,
        Long dailyJogakId,
        String mogakTitle,
        String category,
        String title,
        @JsonProperty("isRoutine") Boolean isRoutine,
        @JsonProperty("isAchievement") Boolean isAchievement
) {
    public static DailyJogakResponse from(DailyJogak dailyJogak) {
        return new DailyJogakResponse(
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getMogak().getTitle(),
                dailyJogak.getCategory().getName(),
                dailyJogak.getTitle(),
                dailyJogak.getIsRoutine(),
                dailyJogak.isSuccess()
        );
    }

    public static DailyJogakResponse fromFutureRoutineJogak(Jogak jogak) {
        return new DailyJogakResponse(
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
