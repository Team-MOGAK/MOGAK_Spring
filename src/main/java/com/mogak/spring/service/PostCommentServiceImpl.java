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
@Transactional
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 생성 - dto
    @Transactional
    @Override
    public PostComment create(CommentRequestDto.CreateCommentDto request, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("user가 존재하지 않습니다"));
        if(request.getContents().length() > 200 ){
            throw new IllegalArgumentException("최대 글자수 200자를 초과하였습니다");
        }
        PostComment comment = CommentConverter.toComment(request,post,user);
        post.putComment(comment);
        return postCommentRepository.save(comment);
    }

    //댓글 조회
    @Override
    public List<PostComment> findByPostId(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 post 입니다"));
        return postCommentRepository.findAllByPost(post);
    }

    //댓글 수정
    @Transactional
    @Override
    public PostComment update(CommentRequestDto.UpdateCommentDto request, Long postId, Long commentId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다"));
        PostComment comment = postCommentRepository.findByPostAndId(post,commentId);
        if(comment == null){
            throw new IllegalArgumentException("존재하지 않는 댓글입니다");
        }
        if(request.getContents().length() > 200 ){
            throw new IllegalArgumentException("최대 글자수 200자를 초과하였습니다");
        }
        comment.updateComment(request.getContents());
        return comment;
    }


    //댓글 삭제
    @Transactional
    @Override
    public void delete(Long postId, Long commentId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다"));
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("comment가 존재하지 않습니다"));
        postCommentRepository.deleteByPostAndId(post, commentId);
        return;
    }

}
