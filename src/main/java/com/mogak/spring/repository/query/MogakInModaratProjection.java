package com.mogak.spring.repository.query;

import com.mogak.spring.domain.mogak.MogakCategory;

public record MogakInModaratProjection(
        String title,
        MogakCategory bigCategory,
        String smallCategory,
        String color
) {
}
