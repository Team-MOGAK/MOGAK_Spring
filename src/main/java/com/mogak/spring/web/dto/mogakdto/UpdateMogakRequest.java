package com.mogak.spring.web.dto.mogakdto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateMogakRequest(
        @NotNull
        Long mogakId,
        @Size(min = 1, max = 100)
        String title,
        @NotNull
        @Size(min = 1, max = 100)
        String bigCategory,
        @Size(min = 1, max = 200)
        String smallCategory,
        @Size(min = 4, max = 10)
        String color
) {
}
