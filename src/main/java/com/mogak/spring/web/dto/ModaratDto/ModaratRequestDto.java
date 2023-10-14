package com.mogak.spring.web.dto.ModaratDto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class ModaratRequestDto {

    @Getter
    public static class CreateModaratDto {
        @NotNull @Max(100)
        private String title;
        @NotNull
        private String color;
    }

}
