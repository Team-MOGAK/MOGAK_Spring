package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.DailyJogak;

import java.time.LocalDate;

public record RoutineJogakResponse(
        Long dailyJogakId,
        LocalDate date,
        @JsonProperty("isAchievement") Boolean isAchievement,
        String title
) {
    public static RoutineJogakResponse from(DailyJogak dailyJogak) {
        return new RoutineJogakResponse(
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                dailyJogak.isSuccess(),
                dailyJogak.getTitle()
        );
    }

    public static RoutineJogakResponse fromFutureRoutineJogak(LocalDate date, String title) {
        return new RoutineJogakResponse(-1L, date, false, title);
    }
}
