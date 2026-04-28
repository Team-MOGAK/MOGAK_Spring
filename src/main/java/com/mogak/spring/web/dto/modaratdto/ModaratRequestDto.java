package com.mogak.spring.web.dto.modaratdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ModaratRequestDto {
    public record CreateModaratDto(
            @NotBlank
            @Size(min = 1, max = 100)
            String title,
            @NotBlank
            @Size(min = 1, max = 100)
            String color
    ) {
    }

    public record UpdateModaratDto(
            @Size(min = 1, max = 100)
            String title,
            @Size(min = 1, max = 100)
            String color
    ) {
    }

}
