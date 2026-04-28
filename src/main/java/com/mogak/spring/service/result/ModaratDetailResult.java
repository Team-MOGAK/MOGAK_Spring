package com.mogak.spring.service.result;

import com.mogak.spring.repository.query.ModaratDetailProjection;

import java.util.List;

public record ModaratDetailResult(Long id, String title, String color, List<MogakInModaratResult> mogaks) {
    public static ModaratDetailResult from(ModaratDetailProjection projection) {
        return new ModaratDetailResult(
                projection.id(),
                projection.title(),
                projection.color(),
                projection.mogaks().stream()
                        .map(MogakInModaratResult::from)
                        .toList()
        );
    }
}
