package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.repository.PostImgRepository;
import com.mogak.spring.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostImgService {

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;

    public List<String> findUrlByPost(Long postId){
        Post post = postRepository.findById(postId).get();
        List<PostImg> postImgList = postImgRepository.findAllByPost(post);
        List<String> imgUrlList =new ArrayList<>();
        for(PostImg postImg :postImgList){
            imgUrlList.add(postImg.getImgUrl());
        }
        return imgUrlList;
    }
}
