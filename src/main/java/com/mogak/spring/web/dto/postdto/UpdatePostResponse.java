package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.post.Post;

import java.time.LocalDateTime;

public record UpdatePostResponse(
        Long id,
        String contents,
        LocalDateTime updatedAt
) {
    public static UpdatePostResponse of(Long id, String contents, LocalDateTime updatedAt) {
        return new UpdatePostResponse(id, contents, updatedAt);
    }

    public static UpdatePostResponse from(Post post, LocalDateTime updatedAt) {
        return of(post.getId(), post.getContents(), updatedAt);
    }
}
