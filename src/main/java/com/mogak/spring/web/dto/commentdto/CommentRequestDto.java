package com.mogak.spring.web.dto.commentdto;

import lombok.Getter;

public class CommentRequestDto {

    @Getter
    public static class CreateCommentDto{
        private String contents;
    }

    @Getter
    public static class UpdateCommentDto{
        private String contents;
    }
}
