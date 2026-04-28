package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;

import java.time.LocalDate;

public record PostSummaryResponse(
        Long postId,
        Long mogakId,
        Long jogakId,
        Long dailyJogakId,
        LocalDate targetDate,
        String contents,
        String thumbnailUrl,
        int likeCnt
) {
    public static PostSummaryResponse of(
            Long postId,
            Long mogakId,
            Long jogakId,
            Long dailyJogakId,
            LocalDate targetDate,
            String contents,
            String thumbnailUrl,
            int likeCnt
    ) {
        return new PostSummaryResponse(postId, mogakId, jogakId, dailyJogakId, targetDate, contents, thumbnailUrl, likeCnt);
    }

    public static PostSummaryResponse from(Post post) {
        DailyJogak dailyJogak = post.getDailyJogak();
        return of(
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
