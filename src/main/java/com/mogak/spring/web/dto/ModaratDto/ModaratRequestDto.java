package com.mogak.spring.web.dto.ModaratDto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class ModaratRequestDto {

    @Getter
    public static class CreateModaratDto {
        @NotNull @Max(100)
        private String title;
        @NotNull @Max(100)
        private String color;
    }

    @Getter
    public static class UpdateModaratDto {
        @Max(100)
        private String title;
        @Max(100)
        private String color;
    }

}
