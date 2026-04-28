package com.mogak.spring.service.result.post;

import java.util.List;

public record PacemakerPostResult(
        NetworkUserResult user,
        String contents,
        List<String> imgUrls,
        List<NetworkCommentResult> comments,
        int likeCnt,
        int viewCnt
) {
    public static PacemakerPostResult of(
            NetworkUserResult user,
            String contents,
            List<String> imgUrls,
            List<NetworkCommentResult> comments,
            int likeCnt,
            int viewCnt
    ) {
        return new PacemakerPostResult(user, contents, imgUrls, comments, likeCnt, viewCnt);
    }
}
