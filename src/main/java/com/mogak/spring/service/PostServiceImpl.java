package com.mogak.spring.service;

import com.mogak.spring.converter.PostConverter;
import com.mogak.spring.converter.PostImgConverter;
import com.mogak.spring.converter.PostLIkeConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostLikeRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final MogakRepository mogakRepository;
    private final UserRepository userRepository;
    private final PostImgRepository postImgRepository;
    private final PostCommentRepository postCommentRepository;

    //회고록 & 회고록 이미지 생성 => 리팩토링 필요
    @Transactional
    @Override
    public Post create(PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList, /*User user,*/Long mogakId){
        Mogak mogak = mogakRepository.findById(mogakId).orElseThrow(() -> new IllegalArgumentException("mogak이 존재하지 않습니다"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("user가 존재하지 않습니다"));
        if(request.getContents().length() > 350){
            throw new IllegalArgumentException("최대 글자수 350자를 초과하였습니다");
        }
        Post post= PostConverter.toPost(request, user, mogak);
        if(postImgDtoList.isEmpty() == true){
            throw new IllegalArgumentException("이미지가 존재하지 않습니다");
        }
        for(PostImgRequestDto.CreatePostImgDto postImgDto : postImgDtoList){
            PostImg postImg = PostImgConverter.toPostImg(postImgDto, post);
            //썸네일이미지인지 체크 필요
            if(postImgDto.isThumbnail()==true){
                post.putPostThumbnailUrl(postImg.getImgUrl()); //썸네일 이미지는 thumbnailurl에 추가
            }
            else{//이미지 업로드 체크
                post.putPostImg(postImg);
            }
            postImgRepository.save(postImg);
        }
        return postRepository.save(post);
    }
    //회고록 조회 - 무한 스크롤
    @Override
    public Slice<Post> getAllPosts(Long lastPostId, Long mogakId, int size){
        Mogak mogak = mogakRepository.findById(mogakId).orElseThrow(()->{return new IllegalArgumentException("mogak이 존재하지 않습니다");
        });
        Pageable pageable = Pageable.ofSize(size);
        Slice<Post> posts = postRepository.findAllPosts(lastPostId!=null?lastPostId:Long.MAX_VALUE, mogakId, pageable);
        return posts;
    }
    //회고록 상세 조회 + 댓글, 이미지 같이 보이게
    @Override
    public Post findById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다"));
        return post;
    }


    //회고록 수정
    @Transactional
    @Override
    public Post update(Long postId, PostRequestDto.UpdatePostDto request){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다"));
        if(request.getContents().length() > 350){
            throw new IllegalArgumentException("최대 글자수 350자를 초과하였습니다");
        }
        post.updatePost(request.getContents());
        return post;
    }

    //회고록 삭제
    @Transactional
    @Override
    public void delete(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다"));
        //이미지 삭제
        postImgRepository.deleteAllByPost(post);
        //댓글 삭제
        postCommentRepository.deleteAllByPost(post);
        //회고록 삭제
        postRepository.deleteById(postId);
    }

}
