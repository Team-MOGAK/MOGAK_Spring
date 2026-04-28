package com.mogak.spring.web.dto.modaratdto;

import com.mogak.spring.domain.modarat.Modarat;

public class ModaratResponseDto {
    public record ModaratDto(Long id, String title, String color) {
        public static ModaratDto from(Modarat modarat) {
            return new ModaratDto(modarat.getId(), modarat.getTitle(), modarat.getColor());
        }
    }
}
