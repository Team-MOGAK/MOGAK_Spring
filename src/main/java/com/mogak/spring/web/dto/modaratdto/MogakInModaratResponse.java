package com.mogak.spring.web.dto.modaratdto;

import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.repository.query.MogakInModaratProjection;

public record MogakInModaratResponse(
        String title,
        MogakCategory bigCategory,
        String smallCategory,
        String color
) {
    public static MogakInModaratResponse from(MogakInModaratProjection projection) {
        return new MogakInModaratResponse(
                projection.title(),
                projection.bigCategory(),
                projection.smallCategory(),
                projection.color()
        );
    }
}
