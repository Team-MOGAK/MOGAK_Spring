package com.mogak.spring.repository.query;

import com.mogak.spring.domain.mogak.MogakCategory;
public record GetMogakInModaratDto(
        String title,
        MogakCategory bigCategory,
        String smallCategory,
        String color
) {
}
