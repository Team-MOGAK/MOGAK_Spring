package com.mogak.spring.web.dto.ModaratDto;

import lombok.*;

public class ModaratResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateModaratDto {
        private String title;
        private String color;
    }
}
