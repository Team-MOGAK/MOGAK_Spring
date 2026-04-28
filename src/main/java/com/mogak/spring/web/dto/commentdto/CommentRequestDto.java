package com.mogak.spring.web.dto.commentdto;

public class CommentRequestDto {

    private CommentRequestDto() {
    }

    public record CreateCommentDto(String contents) {
    }

    public record UpdateCommentDto(String contents) {
    }
}
