package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public class PostConverter {

    public static PostResponseDto.CreatePostDto toCreatePostDto(Post post){
        return PostResponseDto.CreatePostDto.builder()
                .id(post.getId())
                .mogakId(post.getMogak().getId())
                .userId(post.getUser().getId())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
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

    public static PostResponseDto.PostDto toPostDto(Post post, List<String> imgUrls){
        return PostResponseDto.PostDto.builder()
                .postId(post.getId())
                .mogakId(post.getMogak().getId())
                .userId(post.getUser().getId())
                .contents(post.getContents())
                .imgUrls(imgUrls)
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
