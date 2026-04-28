package com.mogak.spring.web.dto.commentdto;

import com.mogak.spring.domain.post.PostComment;

import java.util.List;

public record CommentListResponse(List<CommentResponse> comments) {
    public static CommentListResponse of(List<CommentResponse> comments) {
        return new CommentListResponse(comments);
    }

    public static CommentListResponse from(List<PostComment> commentList) {
        return of(commentList.stream().map(CommentResponse::from).toList());
    }
}
