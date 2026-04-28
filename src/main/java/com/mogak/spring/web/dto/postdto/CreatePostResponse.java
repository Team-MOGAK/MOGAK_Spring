package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreatePostResponse(
        Long id,
        Long mogakId,
        Long jogakId,
        Long dailyJogakId,
        LocalDate targetDate,
        Long userId,
        String contents,
        List<String> imgUrls,
        LocalDateTime createdAt
) {
    public static CreatePostResponse of(
            Long id,
            Long mogakId,
            Long jogakId,
            Long dailyJogakId,
            LocalDate targetDate,
            Long userId,
            String contents,
            List<String> imgUrls,
            LocalDateTime createdAt
    ) {
        return new CreatePostResponse(id, mogakId, jogakId, dailyJogakId, targetDate, userId, contents, imgUrls, createdAt);
    }

    public static CreatePostResponse from(Post post, List<String> imgUrls) {
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
                post.getCreatedAt()
        );
    }
}
