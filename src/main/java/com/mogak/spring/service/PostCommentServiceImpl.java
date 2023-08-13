package com.mogak.spring.service;

import com.mogak.spring.converter.CommentConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.ErrorCode;
import com.mogak.spring.exception.PostCommentException;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.exception.UserException;
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
    public PostComment create(CommentRequestDto.CreateCommentDto request, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if (request.getContents().length() > 200) {
            throw new PostCommentException(ErrorCode.EXCEED_MAX_NUM_COMMENT);
        }
        PostComment comment = CommentConverter.toComment(request,post,user);
        post.putComment(comment);
        return postCommentRepository.save(comment);
    }

    //댓글 조회
    @Override
    public List<PostComment> findByPostId(Long postId) {
        return postCommentRepository.findAllByPost(postId);
    }

    //댓글 수정
    @Transactional
    @Override
    public PostComment update(CommentRequestDto.UpdateCommentDto request, Long postId, Long commentId) {
        PostComment comment = postCommentRepository.findByPostAndId(postId,commentId);
        if (comment == null) {
            throw new PostCommentException(ErrorCode.NOT_EXIST_COMMENT);
        }
        if (request.getContents().length() > 200 ) {
            throw new PostCommentException(ErrorCode.EXCEED_MAX_NUM_COMMENT);
        }
        comment.updateComment(request.getContents());
        return comment;
    }


    //댓글 삭제
    @Transactional
    @Override
    public void delete(Long postId, Long commentId) {
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new PostCommentException(ErrorCode.NOT_EXIST_COMMENT));
        postCommentRepository.deleteByPostAndId(postId, commentId);
    }

}
