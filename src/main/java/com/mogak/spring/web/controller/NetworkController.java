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
    //좋아요 생성&삭제
    @PostMapping("/posts/like")
    public ResponseEntity<String> updateLike(@RequestBody PostLikeRequestDto.LikeDto request){
        String message = postLikeService.updateLike(request);
        return ResponseEntity.ok(message);
    }
}
