package com.mogak.spring.web.dto.jogakdto;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class JogakRequestDto {
    @Getter
    public static class CreateJogakDto {
        @NotNull
        private Long mogakId;
        @NotNull @Size(min = 1, max = 100)
        private String title;
        @NotNull
        private Boolean isRoutine;
        private List<String> days;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate today;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }

    @Getter
    public static class UpdateJogakDto {
        @NotNull @Size(min = 1, max = 100)
        private String title;
        @NotNull
        private Boolean isRoutine;
        private List<String> days;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }

}
