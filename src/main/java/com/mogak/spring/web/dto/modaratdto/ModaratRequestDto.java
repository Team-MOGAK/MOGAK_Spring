package com.mogak.spring.web.dto.modaratdto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ModaratRequestDto {

    @Getter
    public static class CreateModaratDto {
        @NotBlank
        @Size(min = 1, max = 100)
        private String title;
        @NotBlank
        @Size(min = 1, max = 100)
        private String color;
    }

    @Getter
    public static class UpdateModaratDto {
        @Size(min = 1, max = 100)
        private String title;
        @Size(min = 1, max = 100)
        private String color;
    }

}
