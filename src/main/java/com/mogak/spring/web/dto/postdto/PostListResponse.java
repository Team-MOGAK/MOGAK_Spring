package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PostListResponse(
        List<PostSummaryResponse> posts,
        @JsonProperty("hasNext") boolean hasNext,
        Integer size
) {
    public static PostListResponse of(List<PostSummaryResponse> posts, boolean hasNext, Integer size) {
        return new PostListResponse(posts, hasNext, size);
    }
}
