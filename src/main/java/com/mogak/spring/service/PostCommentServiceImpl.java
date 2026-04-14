package com.mogak.spring.service;

import com.mogak.spring.converter.CommentConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostComment;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.PostCommentException;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.PostCommentRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.commentdto.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
        User user = getCurrentUser();
        if (request.getContents().length() > 200) {
            throw new PostCommentException(ErrorCode.EXCEED_MAX_NUM_COMMENT);
        }
        PostComment comment = CommentConverter.toComment(request,post, user);
        post.putComment(comment);
        post.addCommentCnt();
        return postCommentRepository.save(comment);
    }

    //댓글 조회
    @Override
    public List<PostComment> findByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        return postCommentRepository.findAllByPost(post);
    }

    //댓글 수정
    @Transactional
    @Override
    public PostComment update(CommentRequestDto.UpdateCommentDto request, Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        PostComment comment = getCommentInPost(post, commentId);
        validateOwner(comment, getCurrentUser());
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        PostComment comment = getCommentInPost(post, commentId);
        validateOwner(comment, getCurrentUser());
        post.subtractCommentCnt();
        postCommentRepository.delete(comment);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
    }

    private PostComment getCommentInPost(Post post, Long commentId) {
        PostComment comment = postCommentRepository.findByPostAndId(post, commentId);
        if (comment == null) {
            throw new PostCommentException(ErrorCode.NOT_EXIST_COMMENT);
        }
        return comment;
    }

    private void validateOwner(PostComment comment, User user) {
        if (!Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
    }

}
