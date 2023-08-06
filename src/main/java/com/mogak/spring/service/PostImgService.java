package com.mogak.spring.service;

import java.util.List;

public interface PostImgService {

    List<String> findUrlByPost(Long postId);
}
