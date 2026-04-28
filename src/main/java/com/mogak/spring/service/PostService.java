package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkListDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto.PostListDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;

import static com.mogak.spring.web.dto.postdto.PostResponseDto.NetworkPostDto;

public interface PostService {

    void validateCreateAccess(Long userId, PostRequestDto.CreatePostDto request, List<MultipartFile> multipartFile, Long jogakId);
    Post create(Long userId, PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList, Long jogakId);
    Post getByJogakAndTargetDate(Long userId, Long jogakId, LocalDate targetDate);
    PostListDto getAllPosts(Long userId, int page, Long mogakId, int size);
    Post findById(Long userId, Long postId);
    Post update(Long userId, Long postId, PostRequestDto.UpdatePostDto request);
    void delete(Long userId, Long postId);
    List<NetworkPostDto> getPacemakerPosts(Long userId, int cursor, int size);
    NetworkListDto getNetworkPosts(Long userId, int page, int size, String sort, String address);
    List<String> findImgUrlByPost(Long postId);
    List<Long> findActiveCommentIds(Post post);
    List<String> findNotThumbnailImg(Post post);
    List<PostImg> findAllImgByPost(Post post);
}
