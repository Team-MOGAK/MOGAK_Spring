package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostConverter;
import com.mogak.spring.converter.PostImgConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.service.AwsS3Service;
import com.mogak.spring.service.PostImgService;
import com.mogak.spring.service.PostServiceImpl;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;
    private final AwsS3Service awsS3Service;
    private final PostImgService postImgService;
    private static String dirName = "img";

    //create
    @PostMapping("/mogaks/{mogakId}/posts")
    public ResponseEntity<PostResponseDto.CreatePostDto> createPost(@PathVariable Long mogakId, @RequestPart PostRequestDto.CreatePostDto request, @RequestPart(required = true) List<MultipartFile> multipartFile/*User user*/){
        //에러핸들링 필요
        List<PostImgRequestDto.CreatePostImgDto> postImgDtoList = awsS3Service.uploadImg(multipartFile, dirName);
        Post post = postService.create(request, postImgDtoList, mogakId);
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

    //Delete - s3 이미지 삭제,댓글 삭제도 구현
    @DeleteMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.DeletePostDto> deletePost(@PathVariable Long postId){
        Post post = postService.findById(postId);
        List<PostImg> postImgList = postImgService.findAllByPost(post);
        awsS3Service.deleteImg(postImgList,dirName); //s3이미지 객체 삭제
        postService.delete(postId);
        return ResponseEntity.ok(PostConverter.toDeletePostDto());
    }
}
