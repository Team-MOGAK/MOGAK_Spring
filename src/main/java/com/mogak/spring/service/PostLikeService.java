package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.web.dto.PostLikeRequestDto;

public interface PostLikeService {

    PostLike createLike(Long postId, PostLikeRequestDto.CreateLikeDto request);
}
