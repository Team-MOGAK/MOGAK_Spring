package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.service.PostLikeService;
import com.mogak.spring.service.PostService;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import com.mogak.spring.web.dto.PostLikeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "네트워킹 API", description = "네트워킹 API 명세서")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NetworkController {

    private final PostLikeService postLikeService;
    private final PostService postService;

    //좋아요 생성&삭제
    @Operation(summary = "좋아요 생성/삭제", description = "게시물에 좋아요를 생성/삭제합니다",
            parameters = @Parameter(name = "JWT 토큰", description = "jwt 토큰"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "좋아요 생성/삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록, 존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "이미 좋아요를 누른 케이스 Or 서버 오류"),
            })
    @PostMapping("/posts/like")
    public ResponseEntity<String> updateLike(@RequestBody PostLikeRequestDto.LikeDto request, HttpServletRequest req){
        String message = postLikeService.updateLike(request, req);
        return ResponseEntity.ok(message);
    }


}
