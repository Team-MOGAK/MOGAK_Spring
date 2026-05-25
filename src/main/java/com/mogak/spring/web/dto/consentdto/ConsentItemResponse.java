package com.mogak.spring.web.dto.consentdto;

import com.mogak.spring.service.result.ConsentItemResult;

public record ConsentItemResponse(
        Long id,
        String code,
        String name,
        String description,
        Boolean required
) {
    public static ConsentItemResponse from(ConsentItemResult result) {
        return new ConsentItemResponse(
                result.id(),
                result.code(),
                result.name(),
                result.description(),
                result.required()
        );
    }
}
