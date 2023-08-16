package com.mogak.spring.service;

import com.mogak.spring.web.dto.PostLikeRequestDto;

import javax.servlet.http.HttpServletRequest;

public interface PostLikeService {

    String updateLike(PostLikeRequestDto.LikeDto request, HttpServletRequest req);
}
