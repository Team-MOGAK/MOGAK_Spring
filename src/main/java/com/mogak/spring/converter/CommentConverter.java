package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.commentdto.CommentRequestDto;
import com.mogak.spring.web.dto.commentdto.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    public static CommentResponseDto.CreateCommentDto toCreateCommentDto(PostComment comment){
        return CommentResponseDto.CreateCommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .contents(comment.getContents())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static PostComment toComment(CommentRequestDto.CreateCommentDto request, Post post, User user){
        return PostComment.builder()
                .post(post)
                .user(user)
                .contents(request.getContents())
                .validation(Validation.ACTIVE.toString())
                .build();
    }
    public static CommentResponseDto.CommentDto toCommentDto(PostComment comment){
        return CommentResponseDto.CommentDto.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .contents(comment.getContents())
                .createdAt(comment.getCreatedAt())
                .build();
    }
    public static List<CommentResponseDto.CommentDto> toCommentDtoList(List<PostComment> commentList){
        return commentList.stream()
                .map(comment -> toCommentDto(comment))
                .collect(Collectors.toList());
    }
    public static CommentResponseDto.CommentListDto toCommentListDto(List<PostComment> commentList){
        return CommentResponseDto.CommentListDto.builder()
                .commentDtoList(toCommentDtoList(commentList))
                .build();

    }

    public static CommentResponseDto.UpdateCommentDto toUpdateCommentDto(PostComment comment){
        return CommentResponseDto.UpdateCommentDto.builder()
                .id(comment.getId())
                .contents(comment.getContents())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto.DeleteCommentDto toDeleteCommentDto(){
        return CommentResponseDto.DeleteCommentDto.builder()
                .validation(Validation.INACTIVE.toString())
                .build();
    }

    public static CommentResponseDto.NetworkCommentDto toNetworkCommentDto(PostComment comment) {
        return CommentResponseDto.NetworkCommentDto.builder()
                .commentId(comment.getId())
                .nickname(comment.getUser().getNickname())
                .contents(comment.getContents())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
