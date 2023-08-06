package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;

import java.util.List;

public interface PostService {

    Post create(PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList,/*User user*/ Long mogakId);
    Post findById(Long postId);
    Post update(Long postId, PostRequestDto.UpdatePostDto request);
    void delete(Long postId);
}
