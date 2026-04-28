package com.mogak.spring.repository.query;

import java.util.List;

public record ModaratDetailProjection(
        Long id,
        String title,
        String color,
        List<MogakInModaratProjection> mogaks
) {
    public ModaratDetailProjection(Long id, String title, String color) {
        this(id, title, color, List.of());
    }

    public ModaratDetailProjection withMogaks(List<MogakInModaratProjection> mogaks) {
        return new ModaratDetailProjection(id, title, color, mogaks);
    }
}
