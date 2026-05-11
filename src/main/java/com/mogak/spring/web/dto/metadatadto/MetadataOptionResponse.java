package com.mogak.spring.web.dto.metadatadto;

import com.mogak.spring.service.result.metadata.MetadataOptionResult;

public record MetadataOptionResponse(String name) {
    public static MetadataOptionResponse from(MetadataOptionResult result) {
        return new MetadataOptionResponse(result.name());
    }
}
