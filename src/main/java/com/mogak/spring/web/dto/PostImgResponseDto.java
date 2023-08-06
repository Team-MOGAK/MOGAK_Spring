package com.mogak.spring.web.dto;


import lombok.*;

import java.time.LocalDateTime;

public class PostImgResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostImgDto {
        private Long id;
        private Long postId;
        private String imgName;
        private String imgUrl;
        private LocalDateTime createdAt;
    }
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreatePostImgDto {
        private Long id;
        private Long postId;
        private String imgName;
        private String imgUrl;
        private LocalDateTime createdAt;
    }
}
