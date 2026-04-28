package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.post.Post;

import java.util.List;

public record NetworkPostSummaryResponse(
        Long postId,
        String userName,
        String userJob,
        String contents,
        List<String> imgUrls,
        int commentCnt,
        int likeCnt
) {
    public static NetworkPostSummaryResponse of(
            Long postId,
            String userName,
            String userJob,
            String contents,
            List<String> imgUrls,
            int commentCnt,
            int likeCnt
    ) {
        return new NetworkPostSummaryResponse(postId, userName, userJob, contents, imgUrls, commentCnt, likeCnt);
    }

    public static NetworkPostSummaryResponse from(Post post, List<String> imgUrls) {
        return of(
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
