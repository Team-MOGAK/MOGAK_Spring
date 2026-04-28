package com.mogak.spring.web.dto.postdto;

public class PostLikeResponseDto {

    private PostLikeResponseDto() {
    }

    public record CreatePostLikeDto(Long userId, Long postId) {
    }
}
