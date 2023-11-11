package com.mogak.spring.web.dto.postdto;

import lombok.Getter;

public class PostLikeRequestDto {

    @Getter
    public static class LikeDto {
        private Long postId;
    }

}
