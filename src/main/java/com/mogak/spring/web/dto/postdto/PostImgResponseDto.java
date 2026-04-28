package com.mogak.spring.web.dto.postdto;

import java.time.LocalDateTime;

public class PostImgResponseDto {

    private PostImgResponseDto() {
    }

    public record PostImgDto(
            Long id,
            Long postId,
            String imgName,
            String imgUrl,
            LocalDateTime createdAt
    ) {
    }

    public record CreatePostImgDto(
            Long id,
            Long postId,
            String imgName,
            String imgUrl,
            LocalDateTime createdAt
    ) {
    }
}
