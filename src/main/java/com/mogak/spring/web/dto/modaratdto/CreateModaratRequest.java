package com.mogak.spring.web.dto.modaratdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateModaratRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String title,
        @NotBlank
        @Size(min = 1, max = 100)
        String color
) {
}
