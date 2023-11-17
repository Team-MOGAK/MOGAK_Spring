package com.mogak.spring.web.dto.mogakdto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class MogakRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDto {
        @NotNull
        private Long modaratId;
        @NotNull @Max(100)
        private String title;
        @NotNull @Max(100)
        private String bigCategory;
        @Max(200)
        private String smallCategory;
        private LocalDate startAt;
        private LocalDate endAt;
        @Max(10)
        private String color;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDto {
        @NotNull
        private Long mogakId;
        @NotNull @Max(100)
        private String title;
        @NotNull @Max(100)
        private String bigCategory;
        @Max(200)
        private String smallCategory;
        private LocalDate startAt;
        private LocalDate endAt;
        @Max(10)
        private String color;
    }
}
