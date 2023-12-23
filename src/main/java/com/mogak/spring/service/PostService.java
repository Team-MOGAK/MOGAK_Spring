package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkPostDto;

public interface PostService {

    Post create(PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList,Long mogakId);
    Slice<Post> getAllPosts(int page, Long mogakId, int size);
    Post findById(Long postId);
    Post update(Long postId, PostRequestDto.UpdatePostDto request);
    void delete(Long postId);
    List<NetworkPostDto> getPacemakerPosts(int cursor, int size);
    Slice<Post> getNetworkPosts(int page, int size, String sort, String address);
    List<String> findImgUrlByPost(Long postId);
    List<String> findNotThumbnailImg(Post post);
    List<PostImg> findAllImgByPost(Post post);
}
