package com.mogak.spring.service.result;

public record ConsentItemResult(
        Long id,
        String code,
        String name,
        String description,
        Boolean required,
        Integer displayOrder
) {
}
