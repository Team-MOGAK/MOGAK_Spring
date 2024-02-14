package com.mogak.spring.web.dto.mogakdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
        @Schema(description = "모각의 카테고리", example = "자격증")
        @NotNull @Size(min = 1, max = 100)
        private String bigCategory;
        @Size(min = 1, max = 200)
        private String smallCategory;
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
        @Size(min = 1, max = 100)
        private String title;
        @NotNull @Size(min = 1, max = 100)
        private String bigCategory;
        @Size(min = 1, max = 200)
        private String smallCategory;
        @Size(min = 4, max = 10)
        private String color;
    }
}
