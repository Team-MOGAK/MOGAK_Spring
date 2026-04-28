package com.mogak.spring.service.result.post;

import java.util.List;

public record NetworkListResult(
        List<NetworkFeedPostResult> items,
        Integer page,
        Integer size,
        boolean hasNext
) {
    public static NetworkListResult of(List<NetworkFeedPostResult> items, Integer page, Integer size, boolean hasNext) {
        return new NetworkListResult(items, page, size, hasNext);
    }
}
