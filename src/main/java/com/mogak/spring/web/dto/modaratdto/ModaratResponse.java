package com.mogak.spring.web.dto.modaratdto;

import com.mogak.spring.domain.modarat.Modarat;

public record ModaratResponse(Long id, String title, String color) {
    public static ModaratResponse from(Modarat modarat) {
        return new ModaratResponse(modarat.getId(), modarat.getTitle(), modarat.getColor());
    }
}
