package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.service.result.post.NetworkListResult;
import com.mogak.spring.service.result.post.PacemakerPostResult;
import com.mogak.spring.service.result.post.PostListResult;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;

public interface PostService {

    void validateCreateAccess(Long userId, PostRequestDto.CreatePostDto request, List<MultipartFile> multipartFile, Long jogakId);
    Post create(Long userId, PostRequestDto.CreatePostDto request, List<PostImgRequestDto.CreatePostImgDto> postImgDtoList, Long jogakId);
    Post getByJogakAndTargetDate(Long userId, Long jogakId, LocalDate targetDate);
    PostListResult getAllPosts(Long userId, int page, Long mogakId, int size);
    Post findById(Long userId, Long postId);
    Post update(Long userId, Long postId, PostRequestDto.UpdatePostDto request);
    void delete(Long userId, Long postId);
    List<PacemakerPostResult> getPacemakerPosts(Long userId, int cursor, int size);
    NetworkListResult getNetworkPosts(Long userId, int page, int size, String sort, String address);
    List<String> findImgUrlByPost(Long postId);
    List<Long> findActiveCommentIds(Post post);
    List<String> findNotThumbnailImg(Post post);
    List<PostImg> findAllImgByPost(Post post);
}
