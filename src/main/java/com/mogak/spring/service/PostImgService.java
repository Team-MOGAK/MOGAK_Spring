package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;

import java.util.List;

public interface PostImgService {

    List<String> findUrlByPost(Long postId);
    List<String> findNotThumbnailImg(Post post);
    List<PostImg> findAllByPost(Post post);
}
