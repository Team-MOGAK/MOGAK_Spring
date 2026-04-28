package com.mogak.spring.web.dto.commentdto;

import com.mogak.spring.domain.post.PostComment;

import java.time.LocalDateTime;

public record CommentUpdateResponse(
        Long id,
        String contents,
        LocalDateTime updatedAt
) {
    public static CommentUpdateResponse of(Long id, String contents, LocalDateTime updatedAt) {
        return new CommentUpdateResponse(id, contents, updatedAt);
    }

    public static CommentUpdateResponse from(PostComment comment, LocalDateTime updatedAt) {
        return of(comment.getId(), comment.getContents(), updatedAt);
    }
}
