package com.mogak.spring.web.dto.modaratdto;

import lombok.*;

public class ModaratResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ModaratDto {
        private Long id;
        private String title;
        private String color;
    }

}
