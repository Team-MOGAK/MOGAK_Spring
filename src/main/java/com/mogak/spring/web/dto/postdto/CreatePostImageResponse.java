package com.mogak.spring.web.dto.postdto;

import java.time.LocalDateTime;

public record CreatePostImageResponse(
        Long id,
        Long postId,
        String imgName,
        String imgUrl,
        LocalDateTime createdAt
) {
}
