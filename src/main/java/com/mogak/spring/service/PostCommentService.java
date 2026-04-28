package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostComment;

import java.util.List;

public interface PostCommentService {

    PostComment create(Long userId, String contents, Long postId);
    List<PostComment> findByPostId(Long postId);
    PostComment update(Long userId, String contents, Long postId, Long commentId);
    void delete(Long userId, Long postId, Long commentId);


}
