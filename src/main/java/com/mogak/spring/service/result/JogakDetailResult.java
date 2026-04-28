package com.mogak.spring.service.result;

import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;

public record JogakDetailResult(
        Long jogakId,
        String mogakTitle,
        String category,
        String title,
        Boolean isRoutine,
        List<String> days,
        String color,
        Integer achievements,
        LocalDate startDate,
        LocalDate endDate
) {
    public static JogakDetailResult from(Jogak jogak, String color) {
        return from(jogak, color, null);
    }

    public static JogakDetailResult from(Jogak jogak, String color, List<String> days) {
        return new JogakDetailResult(
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
