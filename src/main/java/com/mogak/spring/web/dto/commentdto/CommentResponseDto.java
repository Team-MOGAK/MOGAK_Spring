package com.mogak.spring.web.dto.commentdto;

import com.mogak.spring.domain.post.PostComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        public static CommentDto from(PostComment comment) {
            return CommentDto.builder()
                    .commentId(comment.getId())
                    .postId(comment.getPost().getId())
                    .userId(comment.getUser().getId())
                    .contents(comment.getContents())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }
    //list
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CommentListDto {
        private List<CommentResponseDto.CommentDto> commentDtoList;

        public static CommentListDto from(List<PostComment> commentList) {
            return CommentListDto.builder()
                    .commentDtoList(commentList.stream()
                            .map(CommentDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
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
        private LocalDateTime createdAt;

        public static CreateCommentDto from(PostComment comment) {
            return CreateCommentDto.builder()
                    .id(comment.getId())
                    .postId(comment.getPost().getId())
                    .userId(comment.getUser().getId())
                    .contents(comment.getContents())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateCommentDto {
        private Long id;
        private String contents;
        private LocalDateTime updatedAt;

        public static UpdateCommentDto from(PostComment comment, LocalDateTime updatedAt) {
            return UpdateCommentDto.builder()
                    .id(comment.getId())
                    .contents(comment.getContents())
                    .updatedAt(updatedAt)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeleteCommentDto {
        private boolean deleted;

        public static DeleteCommentDto deleted() {
            return DeleteCommentDto.builder()
                    .deleted(true)
                    .build();
        }
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

        public static NetworkCommentDto from(PostComment comment) {
            return NetworkCommentDto.builder()
                    .commentId(comment.getId())
                    .nickname(comment.getUser().getNickname())
                    .contents(comment.getContents())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }

}
