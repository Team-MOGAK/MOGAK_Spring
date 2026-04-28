package com.mogak.spring.service.command;

import java.time.LocalDate;
import java.util.List;

public record UpdateJogakCommand(String title, Boolean isRoutine, List<String> days, LocalDate endDate) {
}
