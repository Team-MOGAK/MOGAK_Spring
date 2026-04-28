package com.mogak.spring.service.result;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;

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
    public static PostSummaryResult from(Post post) {
        DailyJogak dailyJogak = post.getDailyJogak();
        return new PostSummaryResult(
                post.getId(),
                dailyJogak.getJogak().getMogak().getId(),
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                post.getContents(),
                post.getPostThumbnailUrl(),
                post.getLikeCnt()
        );
    }
}
