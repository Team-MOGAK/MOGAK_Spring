package com.mogak.spring.service;

import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.PostLikeRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        Post post = postRepository.findById(postId).get();
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("user가 존재하지 않습니다"));
        //이미 좋아요를 누른 게시물에 대한 처리
        if(postLikeRepository.findByPostAndUser(post,user).isPresent()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"이미 좋아요를 누른 게시물입니다");
        }
        PostLike postLike = PostLIkeConverter.toPostLike(post, user);
        post.updatePostLike();
        return postLike;
    }
}
