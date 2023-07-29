package com.mogak.spring.service;

import com.mogak.spring.converter.PostConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.repository.PostRepository;
import com.mogak.spring.web.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MogakRepository mogakRepository;

    //회고록 생성 - dto
    @Transactional
    public Post create(PostRequestDto.CreatePostDto request, User user,Long mogakId){
        Mogak mogak = mogakRepository.findById(mogakId).get();
        Post post= PostConverter.toPost(request, user, mogak);
        return postRepository.save(post);
    }

    //회고록 상세 조회
    public Post findById(Long postId){
        return postRepository.findById(postId).get();
    }

    //회고록 조회 - 무한 스크롤

    //회고록 수정
    @Transactional
    public Post update(Long postId, PostRequestDto.UpdatePostDto request){
        Post post = postRepository.findById(postId).get();
        post.updatePost(request.getContents());
        return post;
    }

    //회고록 삭제
    @Transactional
    public void delete(Long postId){
        postRepository.deleteById(postId);
        return;
    }

}
