package com.mogak.spring.service.result.post;

import java.time.LocalDateTime;

public record NetworkCommentResult(
        Long commentId,
        String nickname,
        String contents,
        LocalDateTime createdAt
) {
    public static NetworkCommentResult of(Long commentId, String nickname, String contents, LocalDateTime createdAt) {
        return new NetworkCommentResult(commentId, nickname, contents, createdAt);
    }
}
