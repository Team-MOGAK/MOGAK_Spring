package com.mogak.spring.web.dto.commentdto;

import com.mogak.spring.domain.post.PostComment;

import java.time.LocalDateTime;

public record CommentCreateResponse(
        Long id,
        Long postId,
        Long userId,
        String contents,
        LocalDateTime createdAt
) {
    public static CommentCreateResponse of(Long id, Long postId, Long userId, String contents, LocalDateTime createdAt) {
        return new CommentCreateResponse(id, postId, userId, contents, createdAt);
    }

    public static CommentCreateResponse from(PostComment comment) {
        return of(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }
}
