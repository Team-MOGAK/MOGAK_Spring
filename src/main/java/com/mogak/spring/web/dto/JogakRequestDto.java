package com.mogak.spring.web.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class JogakRequestDto {
    @Getter
    public static class CreateJogakDto {
        private Long mogakId;
        private String title;
        private Boolean isRoutine;
        private List<String> days;
        private LocalDate endDate;
    }

    @Getter
    public static class UpdateJogakDto {
        private String title;
        private Boolean isRoutine;
        private List<String> days;
        private LocalDate endDate;
    }

}
