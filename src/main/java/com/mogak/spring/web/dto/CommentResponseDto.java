package com.mogak.spring.web.dto;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CommentDto {
        private Long commentId;
        private Long postId;
        private Long userId;
        private String contents;
        private LocalDateTime createdAt;
    }
    //list
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CommentListDto {
        private List<CommentResponseDto.CommentDto> commentDtoList;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateCommentDto {
        private Long id;
        private Long postId;
        private Long userId;
        private String contents;
        //private String validation;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateCommentDto {
        private Long id;
        private String contents;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeleteCommentDto {
        private String validation;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NetworkCommentDto {
        private Long commentId;
        private String nickname;
        private String contents;
        private LocalDateTime createdAt;
    }

}
