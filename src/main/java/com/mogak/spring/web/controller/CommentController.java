package com.mogak.spring.web.controller;

import com.mogak.spring.converter.CommentConverter;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.service.PostCommentServiceImpl;
import com.mogak.spring.web.dto.CommentRequestDto;
import com.mogak.spring.web.dto.CommentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "댓글 API", description = "댓글 API 명세서")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostCommentServiceImpl postCommentService;

    //create
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다",
            parameters = {
                    @Parameter(name = "JWT 토큰", description = "jwt 토큰"),
                    @Parameter(name = "postId", description = "게시물 ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 생성"),
                    @ApiResponse(responseCode = "400", description = "200자 제한 초과",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물, 존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto.CreateCommentDto> createComment(@PathVariable Long postId,
                                                                             @RequestBody CommentRequestDto.CreateCommentDto request,
                                                                             HttpServletRequest req) {
        PostComment comment = postCommentService.create(request, postId, req);
        return ResponseEntity.ok(CommentConverter.toCreateCommentDto(comment));
    }

    //read
    @Operation(summary = "댓글 리스트 조회", description = "댓글 리스트를 조회합니다",
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 리스트 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto.CommentListDto> getCommentList(@PathVariable Long postId) {
        List<PostComment> commentList = postCommentService.findByPostId(postId);
        return ResponseEntity.ok(CommentConverter.toCommentListDto(commentList));
    }

    //update
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다",
            parameters = {
                    @Parameter(name = "postId", description = "게시물 ID"),
                    @Parameter(name = "commentId", description = "댓글 ID"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto.UpdateCommentDto> updateComment(@PathVariable(name = "postId") Long postId,
                                                                             @PathVariable(name = "commentId") Long commentId,
                                                                             @RequestBody CommentRequestDto.UpdateCommentDto request) {
        PostComment comment = postCommentService.update(request,postId,commentId);
        return ResponseEntity.ok(CommentConverter.toUpdateCommentDto(comment));
    }

    //delete
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다",
            parameters = {
                    @Parameter(name = "postId", description = "게시물 ID"),
                    @Parameter(name = "commentId", description = "댓글 ID"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않은 게시물, 존재하지 않은 댓글",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto.DeleteCommentDto> deleteComment(@PathVariable(name = "postId") Long postId,
                                                                             @PathVariable(name = "commentId") Long commentId) {
        postCommentService.delete(postId,commentId);
        return ResponseEntity.ok(CommentConverter.toDeleteCommentDto());
    }
}
