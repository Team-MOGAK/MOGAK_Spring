package com.mogak.spring.service.result;

import com.mogak.spring.domain.modarat.Modarat;

public record ModaratSummaryResult(Long id, String title, String color) {
    public static ModaratSummaryResult from(Modarat modarat) {
        return new ModaratSummaryResult(modarat.getId(), modarat.getTitle(), modarat.getColor());
    }
}
