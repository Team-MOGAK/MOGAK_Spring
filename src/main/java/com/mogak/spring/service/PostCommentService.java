package com.mogak.spring.service;

import com.mogak.spring.converter.CommentConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.PostCommentRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 생성 - dto
    @Transactional
    public PostComment create(CommentRequestDto.CreateCommentDto request, Long postId){
        Post post = postRepository.findById(postId).get();
        User user = userRepository.findById(request.getUserId()).get();
        PostComment comment = CommentConverter.toComment(request,post,user);
        return postCommentRepository.save(comment);
    }

    //댓글 조회
    public List<PostComment> findByPostId(Long postId){
        return postCommentRepository.findAllByPost(postId);
    }

    //댓글 수정
    @Transactional
    public PostComment update(CommentRequestDto.UpdateCommentDto request, Long postId, Long commentId){
        PostComment comment = postCommentRepository.findByPostAndId(postId,commentId);
        comment.updateComment(request.getContents());
        return comment;
    }


    //댓글 삭제
    @Transactional
    public void delete(Long postId, Long commentId){
        postCommentRepository.deleteByPostAndId(postId, commentId);
        return;
    }

}
