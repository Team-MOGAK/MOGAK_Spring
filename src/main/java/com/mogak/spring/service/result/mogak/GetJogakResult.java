package com.mogak.spring.service.result.mogak;

import java.time.LocalDate;
import java.util.List;

public record GetJogakResult(
        Long jogakId,
        String mogakTitle,
        String category,
        String title,
        Boolean isRoutine,
        List<String> days,
        Boolean isAlreadyAdded,
        Integer achievements,
        LocalDate startDate,
        LocalDate endDate
) {
}
