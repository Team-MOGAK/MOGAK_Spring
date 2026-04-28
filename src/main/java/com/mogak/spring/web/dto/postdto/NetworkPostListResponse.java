package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NetworkPostListResponse(
        List<NetworkPostSummaryResponse> posts,
        @JsonProperty("hasNext") boolean hasNext,
        Integer size
) {
    public static NetworkPostListResponse of(List<NetworkPostSummaryResponse> posts, boolean hasNext, Integer size) {
        return new NetworkPostListResponse(posts, hasNext, size);
    }
}
