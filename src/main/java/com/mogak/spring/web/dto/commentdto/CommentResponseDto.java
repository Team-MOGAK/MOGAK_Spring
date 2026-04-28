package com.mogak.spring.web.dto.commentdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.post.PostComment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        public static CommentDto from(PostComment comment) {
            return of(
                    comment.getId(),
                    comment.getPost().getId(),
                    comment.getUser().getId(),
                    comment.getContents(),
                    comment.getCreatedAt()
            );
        }
    }

    public record CommentListDto(List<CommentResponseDto.CommentDto> commentDtoList) {
        public static CommentListDto of(List<CommentResponseDto.CommentDto> commentDtoList) {
            return new CommentListDto(commentDtoList);
        }

        public static CommentListDto from(List<PostComment> commentList) {
            return of(commentList.stream()
                    .map(CommentDto::from)
                    .collect(Collectors.toList()));
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

        public static CreateCommentDto from(PostComment comment) {
            return of(
                    comment.getId(),
                    comment.getPost().getId(),
                    comment.getUser().getId(),
                    comment.getContents(),
                    comment.getCreatedAt()
            );
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

        public static UpdateCommentDto from(PostComment comment, LocalDateTime updatedAt) {
            return of(comment.getId(), comment.getContents(), updatedAt);
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

        public static NetworkCommentDto from(PostComment comment) {
            return of(
                    comment.getId(),
                    comment.getUser().getNickname(),
                    comment.getContents(),
                    comment.getCreatedAt()
            );
        }
    }
}
