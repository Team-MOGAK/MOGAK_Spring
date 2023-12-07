package com.mogak.spring.web.dto.mogakdto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class MogakRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDto {
        @NotNull
        private Long modaratId;
        @NotNull @Size(min = 1, max = 100)
        private String title;
        @NotNull @Size(min = 1, max = 100)
        private String bigCategory;
        @Size(min = 1, max = 200)
        private String smallCategory;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startAt;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endAt;
        @Size(min = 4, max = 10)
        private String color;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDto {
        @NotNull
        private Long mogakId;
        @NotNull @Size(min = 1, max = 100)
        private String title;
        @NotNull @Size(min = 1, max = 100)
        private String bigCategory;
        @Size(min = 1, max = 200)
        private String smallCategory;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startAt;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endAt;
        @Size(min = 4, max = 10)
        private String color;
    }
}
