package com.mogak.spring.service;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.ErrorCode;
import com.mogak.spring.exception.PostException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.repository.PostLikeRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.PostLikeRequestDto;
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

    //좋아요 생성
    @Transactional
    @Override
    public PostLike createLike(Long postId, PostLikeRequestDto.CreateLikeDto request){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        //이미 좋아요를 누른 게시물에 대한 처리
        if (postLikeRepository.findByPostAndUser(post,user).isPresent()) {
            throw new PostException(ErrorCode.ALREADY_CREATE_LIKE);
        }
        PostLike postLike = PostLIkeConverter.toPostLike(post, user);
        post.updatePostLike();
        return postLike;
    }
}
