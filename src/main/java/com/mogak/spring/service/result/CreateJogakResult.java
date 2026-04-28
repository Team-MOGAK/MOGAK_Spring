package com.mogak.spring.service.result;

import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;
import java.util.List;

public record CreateJogakResult(
        Long jogakId,
        String mogakTitle,
        String category,
        String title,
        Boolean isRoutine,
        List<String> days,
        Integer achievements,
        LocalDate startDate,
        LocalDate endDate
) {
    public static CreateJogakResult from(Jogak jogak) {
        return from(jogak, null);
    }

    public static CreateJogakResult from(Jogak jogak, List<String> days) {
        return new CreateJogakResult(
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
