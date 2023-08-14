package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.service.PostLikeService;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import com.mogak.spring.web.dto.PostLikeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "네트워킹 API", description = "네트워킹 API 명세서")
@RestController
@RequiredArgsConstructor
public class NetworkController {

    private final PostLikeService postLikeService;

    //좋아요 생성
    @Operation(summary = "좋아요 생성", description = "게시물에 좋아요를 생성합니다",
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록, 존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "이미 좋아요를 누른 케이스 Or 서버 오류"),
            })
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponseDto.CreatePostLikeDto> createLike(@PathVariable Long postId,
                                                                            @RequestBody PostLikeRequestDto.CreateLikeDto request){
        PostLike postLike = postLikeService.createLike(postId, request);
        return ResponseEntity.ok(PostLIkeConverter.toCreatePostLikeDto(postLike));
    }

    //좋아요 삭제
    //@DeleteMapping("/posts/{postId}/like")
    //public ResponseEntity
}
