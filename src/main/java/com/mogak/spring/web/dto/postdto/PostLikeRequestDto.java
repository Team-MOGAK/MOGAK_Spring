package com.mogak.spring.web.dto.postdto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class PostLikeRequestDto {

    @Getter
    public static class LikeDto {
        @NotNull
        private Long postId;
    }

}
