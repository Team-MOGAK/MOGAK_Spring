package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto;
import org.springframework.data.domain.Slice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.mogak.spring.web.dto.PostResponseDto.*;

public interface PostService {

    Post create(PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList,/*User user*/ Long mogakId, HttpServletRequest req);
    Slice<Post> getAllPosts(int page, Long mogakId, int size);
    Post findById(Long postId);
    Post update(Long postId, PostRequestDto.UpdatePostDto request);
    void delete(Long postId);
    List<NetworkPostDto> getPacemakerPosts(int cursor, int size, HttpServletRequest req);
    Slice<Post> getNetworkPosts(int page, int size, String sort, String address, HttpServletRequest req);
}
