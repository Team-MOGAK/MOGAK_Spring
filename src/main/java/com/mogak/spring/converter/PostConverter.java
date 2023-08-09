package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {

    //이미지까지 업로드 잘 되어있는지 확인
    public static PostResponseDto.CreatePostDto toCreatePostDto(Post post){
        return PostResponseDto.CreatePostDto.builder()
                .id(post.getId())
                .mogakId(post.getMogak().getId())
                .userId(post.getUser().getId())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .imgUrls(post.getPostImgs().stream()
                        .map(m -> m.getImgUrl())
                        .collect(Collectors.toList())
                )
                .build();
    }

    public static Post toPost(PostRequestDto.CreatePostDto request, User user, Mogak mogak){
        return Post.builder()
                .mogak(mogak)
                .user(user)
                .contents(request.getContents())
                .likeCnt(1) //더미데이터
                .viewCnt(10) //더미데이터
                .validation(Validation.ACTIVE.toString())
                .build();
    }
    //상세 조회
    public static PostResponseDto.PostDto toPostDto(Post post){
        return PostResponseDto.PostDto.builder()
                .postId(post.getId())
                .mogakId(post.getMogak().getId())
                .userId(post.getUser().getId())
                .contents(post.getContents())
                .imgUrls(post.getPostImgs().stream()
                        .map(m -> m.getImgUrl())
                        .collect(Collectors.toList()))
                .commentId(post.getPostComments().stream()
                        .map(m -> m.getId())
                        .collect(Collectors.toList())) //일단 comment id로 조회하는 것으로 함
                .build();
    }

    public static PostResponseDto.UpdatePostDto toUpdatePostDto(Post post){
        return PostResponseDto.UpdatePostDto.builder()
                .id(post.getId())
                .contents(post.getContents())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PostResponseDto.DeletePostDto toDeletePostDto(){
        return PostResponseDto.DeletePostDto.builder()
                .validation(Validation.INACTIVE.toString())
                .build();
    }


}
