package com.mogak.spring.service;

import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.web.dto.CommentRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostCommentService {

    PostComment create(CommentRequestDto.CreateCommentDto request, Long postId, HttpServletRequest req);
    List<PostComment> findByPostId(Long postId);
    PostComment update(CommentRequestDto.UpdateCommentDto request, Long postId, Long commentId);
    void delete(Long postId, Long commentId);


}
