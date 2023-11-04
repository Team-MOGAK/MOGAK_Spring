package com.mogak.spring.web.dto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class JogakRequestDto {
    @Getter
    public static class CreateJogakDto {
        @NotNull
        private Long mogakId;
        @NotNull @Max(100)
        private String title;
        @NotNull
        private Boolean isRoutine;
        private List<String> days;
        private LocalDate endDate;
    }

    @Getter
    public static class UpdateJogakDto {
        @NotNull @Max(100)
        private String title;
        @NotNull
        private Boolean isRoutine;
        private List<String> days;
        private LocalDate endDate;
    }

}
