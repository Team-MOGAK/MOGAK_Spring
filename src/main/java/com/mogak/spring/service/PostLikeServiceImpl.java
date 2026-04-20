package com.mogak.spring.service;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.PostLikeRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService{

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public String createLike(Long userId, Long postId){
        Post post = getPost(postId);
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        if (postLikeRepository.findByPostAndUser(post,user).isPresent()) {
            throw new PostException(ErrorCode.ALREADY_CREATE_LIKE);
        }
        PostLike postLike = PostLIkeConverter.toPostLike(post, user);
        postLikeRepository.save(postLike);
        post.addPostLike();
        return "좋아요가 생성되었습니다";
    }

    @Transactional
    @Override
    public String deleteLike(Long userId, Long postId) {
        Post post = getPost(postId);
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_LIKE));
        post.subtractPostLike();
        postLikeRepository.delete(postLike);
        return "좋아요가 삭제되었습니다";
    }

    private Post getPost(Long postId) {
        return postRepository.findActiveById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
    }
}
