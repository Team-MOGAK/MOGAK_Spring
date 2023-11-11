package com.mogak.spring.web.dto.postdto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreatePostLikeDto{
        private Long userId;
        private Long postId;
    }



}
