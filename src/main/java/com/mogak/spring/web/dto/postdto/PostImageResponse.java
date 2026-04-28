package com.mogak.spring.web.dto.postdto;

import java.time.LocalDateTime;

public record PostImageResponse(
        Long id,
        Long postId,
        String imgName,
        String imgUrl,
        LocalDateTime createdAt
) {
}
