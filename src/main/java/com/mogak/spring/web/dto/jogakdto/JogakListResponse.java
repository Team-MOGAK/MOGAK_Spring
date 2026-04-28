package com.mogak.spring.web.dto.jogakdto;

import java.util.List;

public record JogakListResponse(int size, List<JogakSummaryResponse> jogaks) {
}
