package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.service.PostLikeService;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import com.mogak.spring.web.dto.PostLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NetworkController {

    private final PostLikeService postLikeService;
    //좋아요 생성
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponseDto.CreatePostLikeDto> createLike(@PathVariable Long postId, @RequestBody PostLikeRequestDto.CreateLikeDto request){
        PostLike postLike = postLikeService.createLike(postId, request);
        return ResponseEntity.ok(PostLIkeConverter.toCreatePostLikeDto(postLike));
    }

    //좋아요 삭제
    //@DeleteMapping("/posts/{postId}/like")
    //public ResponseEntity
}
