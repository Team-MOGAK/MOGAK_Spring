package com.mogak.spring.service.result;

import com.mogak.spring.domain.post.PostComment;

import java.time.LocalDateTime;

public record NetworkCommentResult(Long commentId, String nickname, String contents, LocalDateTime createdAt) {
    public static NetworkCommentResult from(PostComment comment) {
        return new NetworkCommentResult(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }
}
