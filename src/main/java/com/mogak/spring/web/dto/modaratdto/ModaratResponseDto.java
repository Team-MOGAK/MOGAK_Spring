package com.mogak.spring.web.dto.modaratdto;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.MogakCategory;

import java.util.List;

public class ModaratResponseDto {
    public record ModaratDto(Long id, String title, String color) {
        public static ModaratDto from(Modarat modarat) {
            return new ModaratDto(modarat.getId(), modarat.getTitle(), modarat.getColor());
        }
    }

    public record DetailModaratDto(
            Long id,
            String title,
            String color,
            List<MogakInModaratDto> mogakDtoList
    ) {
    }

    public record MogakInModaratDto(
            String title,
            MogakCategory bigCategory,
            String smallCategory,
            String color
    ) {
    }
}
