package com.mogak.spring.service.result;

import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.repository.query.MogakInModaratProjection;

public record MogakInModaratResult(String title, MogakCategory bigCategory, String smallCategory, String color) {
    public static MogakInModaratResult from(MogakInModaratProjection projection) {
        return new MogakInModaratResult(
                projection.title(),
                projection.bigCategory(),
                projection.smallCategory(),
                projection.color()
        );
    }
}
