package com.mogak.spring.web.controller;

import com.mogak.spring.service.FollowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "팔로우 API", description = "팔로우 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{nickname}")
    public ResponseEntity<Void> follow(@PathVariable String nickname, HttpServletRequest req) {
        followService.follow(nickname, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<Void> unfollow(@PathVariable String nickname, HttpServletRequest req) {
        followService.unfollow(nickname, req);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
