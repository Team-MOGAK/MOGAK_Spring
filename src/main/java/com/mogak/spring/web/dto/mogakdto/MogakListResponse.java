package com.mogak.spring.web.dto.mogakdto;

import java.util.List;

public record MogakListResponse(List<MogakResponse> mogaks, int size) {
}
