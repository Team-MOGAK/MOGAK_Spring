package com.mogak.spring.service.result.post;

import java.util.List;

public record PostListResult(
        List<PostSummaryResult> items,
        Integer page,
        Integer size,
        boolean hasNext
) {
    public static PostListResult of(List<PostSummaryResult> items, Integer page, Integer size, boolean hasNext) {
        return new PostListResult(items, page, size, hasNext);
    }
}
