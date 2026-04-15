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
import com.mogak.spring.web.dto.postdto.PostLikeRequestDto;
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

    //좋아요 생성 및 삭제
    @Transactional
    @Override
    public String updateLike(Long userId, PostLikeRequestDto.LikeDto request){
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostException(ErrorCode.NOT_EXIST_POST));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));

        //이미 좋아요를 누른 게시물에 대한 처리
        if (postLikeRepository.findByPostAndUser(post,user).isPresent()) {
            post.subtractPostLike(); //좋아요 수 1 감소
            postLikeRepository.deleteByPostAndUser(post, user);
            return "좋아요가 삭제되었습니다";
        }
        //좋아요를 누른 게시물이 아니라면 좋아요 생성
        PostLike postLike = PostLIkeConverter.toPostLike(post, user);
        postLikeRepository.save(postLike);
        post.addPostLike();
        return "좋아요가 생성되었습니다";
    }
}
