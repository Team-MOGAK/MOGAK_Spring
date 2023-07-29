package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.service.PostService;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //create => mogak id를 path variable로 받아야하지 않나?, 처음 찍은 사진 어케 받아오지
    @PostMapping("/mogaks/{mogakId}/posts")
    public ResponseEntity<PostResponseDto.CreatePostDto> createPost(@PathVariable Long mogakId,@RequestBody PostRequestDto.CreatePostDto request, User user){
        Post post = postService.create(request,user,mogakId); //request에 userid 받아오게
        return ResponseEntity.ok(PostConverter.toCreatePostDto(post));
    }

    //read - 무한 스크롤로 구현
    //@GetMapping("/mogaks/posts")

    //read-상세 조회
    @GetMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.PostDto> getPostDetail(@PathVariable Long postId){
        Post post= postService.findById(postId);
        return ResponseEntity.ok(PostConverter.toPostDto(post));
    }

    //update - 권한 설정 필요
    @PutMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.UpdatePostDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto.UpdatePostDto request){
        Post post = postService.update(postId, request);
        return ResponseEntity.ok(PostConverter.toUpdatePostDto(post));
    }

    //Delete -soft delete 필요
    @DeleteMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.DeletePostDto> deletePost(@PathVariable Long postId){
        postService.delete(postId);
        return ResponseEntity.ok(PostConverter.toDeletePostDto());
    }
}
