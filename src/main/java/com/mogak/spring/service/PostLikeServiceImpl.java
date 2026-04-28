package com.mogak.spring.service;

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
    public String updateLike(Long userId, Long postId){
        Post post = getActivePostForUpdate(postId);
        User user = getActiveUser(userId);
        return toggleLike(post, user);
    }

    private String toggleLike(Post post, User user) {
        if (postLikeRepository.findByPostAndUser(post,user).isPresent()) {
            post.subtractPostLike();
            postLikeRepository.deleteByPostAndUser(post, user);
            return "좋아요가 삭제되었습니다";
        }
        PostLike postLike = PostLike.of(post, user);
        postLikeRepository.save(postLike);
        post.addPostLike();
        return "좋아요가 생성되었습니다";
    }

    private Post getActivePostForUpdate(Long postId) {
        return postRepository.findActiveByIdForUpdate(requirePostId(postId))
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
    }

    private Long requirePostId(Long postId) {
        if (postId == null) {
            throw new PostException(ErrorCode.INVALID_PARAMETER_ERROR);
        }
        return postId;
    }

    private User getActiveUser(Long userId) {
        return userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
    }
}
