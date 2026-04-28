package com.mogak.spring.service.result.post;

import java.util.List;

public record NetworkFeedPostResult(
        Long postId,
        String userName,
        String userJob,
        String contents,
        List<String> imgUrls,
        int commentCnt,
        int likeCnt
) {
    public static NetworkFeedPostResult of(
            Long postId,
            String userName,
            String userJob,
            String contents,
            List<String> imgUrls,
            int commentCnt,
            int likeCnt
    ) {
        return new NetworkFeedPostResult(postId, userName, userJob, contents, imgUrls, commentCnt, likeCnt);
    }
}
