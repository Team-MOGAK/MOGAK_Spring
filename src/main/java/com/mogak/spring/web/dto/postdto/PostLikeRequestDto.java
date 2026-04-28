package com.mogak.spring.web.dto.postdto;

import jakarta.validation.constraints.NotNull;

public class PostLikeRequestDto {

    private PostLikeRequestDto() {
    }

    public record LikeDto(@NotNull Long postId) {
    }
}
