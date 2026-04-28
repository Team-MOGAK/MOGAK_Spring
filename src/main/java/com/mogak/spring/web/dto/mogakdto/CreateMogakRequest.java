package com.mogak.spring.web.dto.mogakdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMogakRequest(
        @NotNull
        Long modaratId,
        @NotNull
        @Size(min = 1, max = 100)
        String title,
        @Schema(description = "모각의 카테고리", example = "자격증")
        @NotNull
        @Size(min = 1, max = 100)
        String bigCategory,
        @Size(min = 1, max = 200)
        String smallCategory,
        @Size(min = 4, max = 10)
        String color
) {
}
