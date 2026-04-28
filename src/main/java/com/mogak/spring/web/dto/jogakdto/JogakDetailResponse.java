package com.mogak.spring.web.dto.jogakdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;

public record JogakDetailResponse(
        Long jogakId,
        String mogakTitle,
        String category,
        String title,
        @JsonProperty("isRoutine") Boolean isRoutine,
        List<String> days,
        String color,
        Integer achievements,
        LocalDate startDate,
        LocalDate endDate
) {
    public static JogakDetailResponse from(Jogak jogak, String color) {
        return from(jogak, color, null);
    }

    public static JogakDetailResponse from(Jogak jogak, String color, List<String> days) {
        return new JogakDetailResponse(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                days,
                color,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }
}
