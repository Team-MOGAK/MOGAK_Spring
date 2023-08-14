package com.mogak.spring.web.dto;

import lombok.Getter;

public class PostLikeRequestDto {

    @Getter
    public static class LikeDto{
        private Long userId;
        private Long postId;
    }

}
