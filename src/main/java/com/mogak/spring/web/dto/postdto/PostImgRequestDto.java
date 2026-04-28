package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostImgRequestDto {

    private PostImgRequestDto() {
    }

    public record CreatePostImgDto(
            String imgName,
            String imgUrl,
            @JsonProperty("thumbnail") boolean thumbnail
    ) {
    }
}
