package com.mogak.spring.service;

public interface PostLikeService {

    String createLike(Long userId, Long postId);
    String deleteLike(Long userId, Long postId);
}
