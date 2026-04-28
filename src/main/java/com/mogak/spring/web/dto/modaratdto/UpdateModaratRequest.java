package com.mogak.spring.web.dto.modaratdto;

import jakarta.validation.constraints.Size;

public record UpdateModaratRequest(
        @Size(min = 1, max = 100)
        String title,
        @Size(min = 1, max = 100)
        String color
) {
}
