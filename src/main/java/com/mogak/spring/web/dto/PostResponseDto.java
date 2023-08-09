package com.mogak.spring.web.dto;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {

    //read-상세조회
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostDto {
        private Long postId;
        private Long mogakId;
        private Long userId; //추후 로그인 구현후 수정 필요할듯
        private String contents;
        private List<String> imgUrls;
        private List<Long> commentId;
        //추후 좋아요수, 조회수 추가
    }
    //list 만들어야하나?

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreatePostDto {
        private Long id;
        private Long mogakId;
        private Long userId;
        private String contents;
        private String validation;
        private List<String> imgUrls;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdatePostDto {
        private Long id;
        private String contents;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeletePostDto {
        private String validation;
    }
}
