package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;

public record CreateJogakResponse(
        Long jogakId,
        String mogakTitle,
        String category,
        String title,
        @JsonProperty("isRoutine") Boolean isRoutine,
        List<String> days,
        Integer achievements,
        LocalDate startDate,
        LocalDate endDate
) {
    public static CreateJogakResponse from(Jogak jogak) {
        return from(jogak, null);
    }

    public static CreateJogakResponse from(Jogak jogak, List<String> days) {
        return new CreateJogakResponse(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                days,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }
}
