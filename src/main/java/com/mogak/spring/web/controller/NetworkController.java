package com.mogak.spring.web.controller;


import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.service.PostLikeService;
import com.mogak.spring.service.PostService;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import com.mogak.spring.web.dto.PostResponseDto.NetworkPostDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "네트워킹 API", description = "네트워킹 API 명세서")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NetworkController {

    private final PostLikeService postLikeService;
    private final PostService postService;

    //좋아요 생성&삭제
    @Operation(summary = "좋아요 생성/삭제",
            description = "게시물에 좋아요를 생성/삭제합니다",
            security = {@SecurityRequirement(name = "Bearer Authentication")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "좋아요 생성/삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록, 존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "이미 좋아요를 누른 케이스 Or 서버 오류"),
            })
    @PostMapping("/api/posts/like")
    public ResponseEntity<BaseResponse<String>> updateLike(@RequestBody PostLikeRequestDto.LikeDto request, HttpServletRequest req){
        String message = postLikeService.updateLike(request, req);
        return ResponseEntity.ok(new BaseResponse<>(message));
    }

    @Operation(summary = "페이스 메이커 게시물 조회",
            description = "팔로우 중인 페이스 메이커의 게시물을 페이징 조회 합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "cursor", description = "페이징 커서(시작점)"),
                    @Parameter(name = "size", description = "페이징 게시물 개수")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류"),
            })
    @GetMapping("/api/posts/pacemakers")
    public ResponseEntity<BaseResponse<List<NetworkPostDto>>> getPacemakerPosts(@RequestParam int cursor,
                                                                  @RequestParam int size,
                                                                  HttpServletRequest req) {
        return ResponseEntity.ok(new BaseResponse<>(postService.getPacemakerPosts(cursor, size, req)));
    }

}
