package com.mogak.spring.service.result.post;

import java.time.LocalDate;

public record PostSummaryResult(
        Long postId,
        Long mogakId,
        Long jogakId,
        Long dailyJogakId,
        LocalDate targetDate,
        String contents,
        String thumbnailUrl,
        int likeCnt
) {
}
