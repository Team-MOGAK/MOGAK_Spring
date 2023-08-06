package com.mogak.spring.web.controller;

import com.mogak.spring.converter.CommentConverter;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.service.PostCommentServiceImpl;
import com.mogak.spring.web.dto.CommentRequestDto;
import com.mogak.spring.web.dto.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostCommentServiceImpl postCommentService;

    //create
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto.CreateCommentDto> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto.CreateCommentDto request){
        PostComment comment = postCommentService.create(request, postId);
        return ResponseEntity.ok(CommentConverter.toCreateCommentDto(comment));
    }

    //read
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto.CommentListDto> getCommentList(@PathVariable Long postId){
        List<PostComment> commentList = postCommentService.findByPostId(postId);
        return ResponseEntity.ok(CommentConverter.toCommentListDto(commentList));
    }

    //update
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto.UpdateCommentDto> updateComment(@PathVariable(name="postId") Long postId, @PathVariable(name = "commentId") Long commentId, @RequestBody CommentRequestDto.UpdateCommentDto request){
        PostComment comment = postCommentService.update(request,postId,commentId);
        return ResponseEntity.ok(CommentConverter.toUpdateCommentDto(comment));
    }

    //delete
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto.DeleteCommentDto> deleteComment(@PathVariable(name="postId") Long postId, @PathVariable(name = "commentId") Long commentId){
        postCommentService.delete(postId,commentId);
        return ResponseEntity.ok(CommentConverter.toDeleteCommentDto());
    }
}
