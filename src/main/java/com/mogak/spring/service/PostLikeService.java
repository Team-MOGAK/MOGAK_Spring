package com.mogak.spring.service;

import com.mogak.spring.web.dto.postdto.PostLikeRequestDto;

public interface PostLikeService {

    String updateLike(PostLikeRequestDto.LikeDto request);
}
