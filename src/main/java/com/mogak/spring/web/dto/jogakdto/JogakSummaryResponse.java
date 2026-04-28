package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.Jogak;
import java.time.LocalDate;
import java.util.List;

public record JogakSummaryResponse(
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
    public static JogakSummaryResponse from(Jogak jogak, Boolean isAlreadyAdded) {
        return new JogakSummaryResponse(
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
}
