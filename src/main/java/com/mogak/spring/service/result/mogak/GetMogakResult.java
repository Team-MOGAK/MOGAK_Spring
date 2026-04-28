package com.mogak.spring.service.result.mogak;

import com.mogak.spring.domain.mogak.MogakCategory;

public record GetMogakResult(
        Long id,
        String title,
        MogakCategory bigCategory,
        String smallCategory,
        String color
) {
}
