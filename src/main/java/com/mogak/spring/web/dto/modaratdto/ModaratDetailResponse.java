package com.mogak.spring.web.dto.modaratdto;

import com.mogak.spring.repository.query.ModaratDetailProjection;

import java.util.List;

public record ModaratDetailResponse(
        Long id,
        String title,
        String color,
        List<MogakInModaratResponse> mogaks
) {
    public static ModaratDetailResponse from(ModaratDetailProjection projection) {
        return new ModaratDetailResponse(
                projection.id(),
                projection.title(),
                projection.color(),
                projection.mogaks().stream()
                        .map(MogakInModaratResponse::from)
                        .toList()
        );
    }
}
