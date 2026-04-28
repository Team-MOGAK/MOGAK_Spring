package com.mogak.spring.service.result.jogak;

import com.mogak.spring.domain.jogak.Jogak;

import java.time.LocalDate;

public record OneTimeJogakResult(
        Long jogakId,
        String mogakTitle,
        String category,
        String title,
        Boolean isRoutine,
        Boolean isAlreadyAdded,
        Integer achievements,
        LocalDate startDate,
        LocalDate endDate
) {
    public static OneTimeJogakResult of(Jogak jogak, Boolean isAlreadyAdded) {
        return new OneTimeJogakResult(
                jogak.getId(),
                jogak.getMogak().getTitle(),
                jogak.getCategory().getName(),
                jogak.getTitle(),
                jogak.getIsRoutine(),
                isAlreadyAdded,
                jogak.getAchievements(),
                jogak.getStartAt(),
                jogak.getEndAt()
        );
    }
}
