package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.service.PostLikeService;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import com.mogak.spring.web.dto.PostLikeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NetworkController {

    private final PostLikeService postLikeService;
    //좋아요 생성
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponseDto.CreatePostLikeDto> createLike(@PathVariable Long postId, @RequestBody PostLikeRequestDto.CreateLikeDto request){
        PostLike postLike = postLikeService.createLike(postId, request);
        return ResponseEntity.ok(PostLIkeConverter.toCreatePostLikeDto(postLike));
    }

    @GetMapping("/test")
    public void test(){
        log.debug("DEBUG");
        log.info("INFO");
        log.warn("WARN");
        log.error("ERROR");
    }
    //좋아요 삭제
    //@DeleteMapping("/posts/{postId}/like")
    //public ResponseEntity
}
