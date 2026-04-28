package com.mogak.spring.service.result.jogak;

import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;

public record DetailJogakResult(
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
    public static DetailJogakResult of(Jogak jogak, String color, List<String> days) {
        return new DetailJogakResult(
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
