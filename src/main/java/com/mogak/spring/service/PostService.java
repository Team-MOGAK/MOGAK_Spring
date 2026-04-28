package com.mogak.spring.service;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.service.result.NetworkPostResult;
import com.mogak.spring.service.result.NetworkPostSummaryResult;
import com.mogak.spring.service.result.PostSummaryResult;
import com.mogak.spring.service.result.UploadedPostImageResult;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;

public interface PostService {

    void validateCreateAccess(Long userId, LocalDate targetDate, String contents, List<MultipartFile> multipartFile, Long jogakId);
    Post create(Long userId, LocalDate targetDate, String contents, List<UploadedPostImageResult> uploadedImages, Long jogakId);
    Post getByJogakAndTargetDate(Long userId, Long jogakId, LocalDate targetDate);
    Slice<PostSummaryResult> getAllPosts(Long userId, int page, Long mogakId, int size);
    Post findById(Long userId, Long postId);
    Post update(Long userId, Long postId, String contents);
    void delete(Long userId, Long postId);
    List<NetworkPostResult> getPacemakerPosts(Long userId, int cursor, int size);
    Slice<NetworkPostSummaryResult> getNetworkPosts(Long userId, int page, int size, String sort, String address);
    List<String> findImgUrlByPost(Long postId);
    List<Long> findActiveCommentIds(Post post);
    List<String> findNotThumbnailImg(Post post);
    List<PostImg> findAllImgByPost(Post post);
}
