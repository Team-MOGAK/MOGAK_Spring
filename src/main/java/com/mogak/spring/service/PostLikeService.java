package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import org.springframework.http.ResponseEntity;

public interface PostLikeService {

    String updateLike(PostLikeRequestDto.LikeDto request);
}
