package com.mogak.spring.web.dto.commentdto;

import com.mogak.spring.domain.post.PostComment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        Long userId,
        String contents,
        LocalDateTime createdAt
) {
    public static CommentResponse of(Long commentId, Long postId, Long userId, String contents, LocalDateTime createdAt) {
        return new CommentResponse(commentId, postId, userId, contents, createdAt);
    }

    public static CommentResponse from(PostComment comment) {
        return of(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }
}
