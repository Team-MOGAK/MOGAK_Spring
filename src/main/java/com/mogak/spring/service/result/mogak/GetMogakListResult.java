package com.mogak.spring.service.result.mogak;

import java.util.List;

public record GetMogakListResult(
        List<GetMogakResult> mogaks,
        int size
) {
}
