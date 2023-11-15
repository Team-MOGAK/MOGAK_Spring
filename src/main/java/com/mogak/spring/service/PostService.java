package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.mogak.spring.web.dto.PostResponseDto.NetworkPostDto;

public interface PostService {

    Post create(Long userId, PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList,/*User user*/ Long mogakId);
    Slice<Post> getAllPosts(int page, Long mogakId, int size);
    Post findById(Long postId);
    Post update(Long postId, PostRequestDto.UpdatePostDto request);
    void delete(Long postId);
    List<NetworkPostDto> getPacemakerPosts(Long userId, int cursor, int size);
    Slice<Post> getNetworkPosts(Long userId, int page, int size, String sort, String address);
    List<String> findImgUrlByPost(Long postId);
    List<String> findNotThumbnailImg(Post post);
    List<PostImg> findAllImgByPost(Post post);
}
