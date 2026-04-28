package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;

import java.time.LocalDate;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        Long mogakId,
        Long jogakId,
        Long dailyJogakId,
        LocalDate targetDate,
        Long userId,
        String contents,
        List<String> imgUrls,
        List<Long> commentId,
        int likeCnt,
        int commentCnt
) {
    public static PostDetailResponse of(
            Long postId,
            Long mogakId,
            Long jogakId,
            Long dailyJogakId,
            LocalDate targetDate,
            Long userId,
            String contents,
            List<String> imgUrls,
            List<Long> commentId,
            int likeCnt,
            int commentCnt
    ) {
        return new PostDetailResponse(postId, mogakId, jogakId, dailyJogakId, targetDate, userId, contents, imgUrls, commentId, likeCnt, commentCnt);
    }

    public static PostDetailResponse from(Post post, List<String> imgUrls, List<Long> commentIds) {
        DailyJogak dailyJogak = post.getDailyJogak();
        return of(
                post.getId(),
                dailyJogak.getJogak().getMogak().getId(),
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                post.getUser().getId(),
                post.getContents(),
                imgUrls,
                commentIds,
                post.getLikeCnt(),
                post.getCommentCnt()
        );
    }
}
