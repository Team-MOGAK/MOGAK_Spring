package com.mogak.spring.service.result;

import com.mogak.spring.domain.post.Post;

import java.util.List;

public record NetworkPostSummaryResult(
        Long postId,
        String userName,
        String userJob,
        String contents,
        List<String> imgUrls,
        int commentCnt,
        int likeCnt
) {
    public static NetworkPostSummaryResult from(Post post, List<String> imgUrls) {
        return new NetworkPostSummaryResult(
                post.getId(),
                post.getUser().getNickname(),
                post.getUser().getJob().getName(),
                post.getContents(),
                imgUrls,
                post.getCommentCnt(),
                post.getLikeCnt()
        );
    }
}
