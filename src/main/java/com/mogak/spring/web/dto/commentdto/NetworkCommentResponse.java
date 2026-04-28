package com.mogak.spring.web.dto.commentdto;

import com.mogak.spring.domain.post.PostComment;

import java.time.LocalDateTime;

public record NetworkCommentResponse(
        Long commentId,
        String nickname,
        String contents,
        LocalDateTime createdAt
) {
    public static NetworkCommentResponse of(Long commentId, String nickname, String contents, LocalDateTime createdAt) {
        return new NetworkCommentResponse(commentId, nickname, contents, createdAt);
    }

    public static NetworkCommentResponse from(PostComment comment) {
        return of(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }
}
