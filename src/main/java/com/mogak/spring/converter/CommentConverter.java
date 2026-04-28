package com.mogak.spring.converter;

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
        return CommentResponseDto.CreateCommentDto.of(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }

    public static PostComment toComment(CommentRequestDto.CreateCommentDto request, Post post, User user){
        return PostComment.builder()
                .post(post)
                .user(user)
                .contents(request.contents())
                .build();
    }
    public static CommentResponseDto.CommentDto toCommentDto(PostComment comment){
        return CommentResponseDto.CommentDto.of(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }
    public static List<CommentResponseDto.CommentDto> toCommentDtoList(List<PostComment> commentList){
        return commentList.stream()
                .map(comment -> toCommentDto(comment))
                .collect(Collectors.toList());
    }
    public static CommentResponseDto.CommentListDto toCommentListDto(List<PostComment> commentList){
        return CommentResponseDto.CommentListDto.of(toCommentDtoList(commentList));

    }

    public static CommentResponseDto.UpdateCommentDto toUpdateCommentDto(PostComment comment){
        return CommentResponseDto.UpdateCommentDto.of(comment.getId(), comment.getContents(), LocalDateTime.now());
    }

    public static CommentResponseDto.DeleteCommentDto toDeleteCommentDto(){
        return CommentResponseDto.DeleteCommentDto.deletedResponse();
    }

    public static CommentResponseDto.NetworkCommentDto toNetworkCommentDto(PostComment comment) {
        return CommentResponseDto.NetworkCommentDto.of(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContents(),
                comment.getCreatedAt()
        );
    }
}
