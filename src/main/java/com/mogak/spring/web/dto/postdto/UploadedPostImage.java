package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadedPostImage(
        String imgName,
        String imgUrl,
        @JsonProperty("thumbnail") boolean thumbnail
) {
}
