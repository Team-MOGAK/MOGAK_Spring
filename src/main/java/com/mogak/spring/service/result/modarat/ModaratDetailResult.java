package com.mogak.spring.service.result.modarat;

import com.mogak.spring.domain.mogak.MogakCategory;

import java.util.List;

public record ModaratDetailResult(
        Long id,
        String title,
        String color,
        List<MogakInModaratResult> mogaks
) {
    public record MogakInModaratResult(
            String title,
            MogakCategory bigCategory,
            String smallCategory,
            String color
    ) {
    }
}
