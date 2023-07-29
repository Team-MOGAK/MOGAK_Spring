package com.mogak.spring.web.dto;

import lombok.Getter;

public class CommentRequestDto {

    @Getter
    public static class CreateCommentDto{
        private Long userId; //추후 수정 필요할듯
        private String contents;
    }

    @Getter
    public static class UpdateCommentDto{
        private String contents;
    }
}
