package com.mogak.spring.web.dto;

import lombok.*;

import java.time.LocalDate;

public class MogakRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDto {
        private String title;
        private String bigCategory;
        private String smallCategory;
        private LocalDate startAt;
        private LocalDate endAt;
        private String color;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDto {
        private Long mogakId;
        private String title;
        private String category;
        private String otherCategory;
        private LocalDate startAt;
        private LocalDate endAt;
    }
}
