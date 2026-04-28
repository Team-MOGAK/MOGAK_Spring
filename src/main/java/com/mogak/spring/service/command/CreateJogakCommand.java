package com.mogak.spring.service.command;

import java.time.LocalDate;
import java.util.List;

public record CreateJogakCommand(
        Long mogakId,
        String title,
        Boolean isRoutine,
        List<String> days,
        LocalDate today,
        LocalDate endDate
) {
}
