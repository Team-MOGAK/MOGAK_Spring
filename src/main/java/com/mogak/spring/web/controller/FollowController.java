package com.mogak.spring.web.controller;

import com.mogak.spring.service.FollowService;
import com.mogak.spring.web.dto.FollowRequestDto;
import com.mogak.spring.web.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.mogak.spring.web.dto.UserResponseDto.*;

@Tag(name = "팔로우 API", description = "팔로우 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/follows/")
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

    @GetMapping("/counts/{user}")
    public ResponseEntity<FollowRequestDto.CountDto> getFollowCount(@PathVariable String user) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getFollowCount(user));
    }

    @GetMapping("/{nickname}/followings")
    public ResponseEntity<List<UserDto>> getMotoList(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getMotoList(nickname));
    }

    @GetMapping("/{nickname}/followers")
    public ResponseEntity<List<UserDto>> getMentorList(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getMentorList(nickname));
    }
}
