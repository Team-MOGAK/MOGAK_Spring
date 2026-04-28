package com.mogak.spring.web.dto.modaratdto;

import com.mogak.spring.domain.modarat.Modarat;
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

        public static ModaratDto from(Modarat modarat) {
            return ModaratDto.builder()
                    .id(modarat.getId())
                    .title(modarat.getTitle())
                    .color(modarat.getColor())
                    .build();
        }
    }

}
