package com.mogak.spring.web.controller;

import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.service.PostCommentService;
import com.mogak.spring.web.dto.commentdto.CommentCreateRequest;
import com.mogak.spring.web.dto.commentdto.CommentCreateResponse;
import com.mogak.spring.web.dto.commentdto.CommentDeleteResponse;
import com.mogak.spring.web.dto.commentdto.CommentListResponse;
import com.mogak.spring.web.dto.commentdto.CommentUpdateRequest;
import com.mogak.spring.web.dto.commentdto.CommentUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "댓글 API", description = "댓글 API 명세서")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostCommentService postCommentService;

    //create
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
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
    public ResponseEntity<BaseResponse<CommentCreateResponse>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody CommentCreateRequest request
            ) {
        PostComment comment = postCommentService.create(authenticatedUser.getUserId(), request.contents(), postId);
        return ResponseEntity.ok(new BaseResponse<>(CommentCreateResponse.from(comment)));
    }

    //read
    @Operation(summary = "댓글 리스트 조회", description = "댓글 리스트를 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 리스트 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<BaseResponse<CommentListResponse>> getCommentList(@PathVariable Long postId) {
        List<PostComment> commentList = postCommentService.findByPostId(postId);
        return ResponseEntity.ok(new BaseResponse<>(CommentListResponse.from(commentList)));
    }

    //update
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "postId", description = "게시물 ID"),
                    @Parameter(name = "commentId", description = "댓글 ID"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
                    @ApiResponse(responseCode = "403", description = "댓글 수정 권한 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물, 존재하지 않는 댓글",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<BaseResponse<CommentUpdateResponse>> updateComment(@PathVariable(name = "postId") Long postId,
                                                                        @PathVariable(name = "commentId") Long commentId,
                                                                        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                        @RequestBody CommentUpdateRequest request) {
        PostComment comment = postCommentService.update(authenticatedUser.getUserId(), request.contents(), postId, commentId);
        return ResponseEntity.ok(new BaseResponse<>(CommentUpdateResponse.from(comment, LocalDateTime.now())));
    }

    //delete
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "postId", description = "게시물 ID"),
                    @Parameter(name = "commentId", description = "댓글 ID"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "댓글 삭제 권한 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물, 존재하지 않는 댓글",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<BaseResponse<CommentDeleteResponse>> deleteComment(@PathVariable(name = "postId") Long postId,
                                                                        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                        @PathVariable(name = "commentId") Long commentId) {
        postCommentService.delete(authenticatedUser.getUserId(), postId, commentId);
        return ResponseEntity.ok(new BaseResponse<>(CommentDeleteResponse.deletedResponse()));
    }
}
