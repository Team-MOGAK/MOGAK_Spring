package com.mogak.spring.web.dto.commentdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDto {

    private CommentResponseDto() {
    }

    public record CommentDto(
            Long commentId,
            Long postId,
            Long userId,
            String contents,
            LocalDateTime createdAt
    ) {
        public static CommentDto of(Long commentId, Long postId, Long userId, String contents, LocalDateTime createdAt) {
            return new CommentDto(commentId, postId, userId, contents, createdAt);
        }
    }

    public record CommentListDto(List<CommentResponseDto.CommentDto> commentDtoList) {
        public static CommentListDto of(List<CommentResponseDto.CommentDto> commentDtoList) {
            return new CommentListDto(commentDtoList);
        }
    }

    public record CreateCommentDto(
            Long id,
            Long postId,
            Long userId,
            String contents,
            LocalDateTime createdAt
    ) {
        public static CreateCommentDto of(Long id, Long postId, Long userId, String contents, LocalDateTime createdAt) {
            return new CreateCommentDto(id, postId, userId, contents, createdAt);
        }
    }

    public record UpdateCommentDto(
            Long id,
            String contents,
            LocalDateTime updatedAt
    ) {
        public static UpdateCommentDto of(Long id, String contents, LocalDateTime updatedAt) {
            return new UpdateCommentDto(id, contents, updatedAt);
        }
    }

    public record DeleteCommentDto(@JsonProperty("deleted") boolean deleted) {
        public static DeleteCommentDto deletedResponse() {
            return new DeleteCommentDto(true);
        }
    }

    public record NetworkCommentDto(
            Long commentId,
            String nickname,
            String contents,
            LocalDateTime createdAt
    ) {
        public static NetworkCommentDto of(Long commentId, String nickname, String contents, LocalDateTime createdAt) {
            return new NetworkCommentDto(commentId, nickname, contents, createdAt);
        }
    }
}
