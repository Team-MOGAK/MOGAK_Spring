package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.web.dto.commentdto.NetworkCommentResponse;
import com.mogak.spring.web.dto.userdto.UserSummaryResponse;

import java.util.List;

public record NetworkPostResponse(
        UserSummaryResponse user,
        String contents,
        List<String> imgUrls,
        List<NetworkCommentResponse> comments,
        int likeCnt,
        int viewCnt
) {
    public static NetworkPostResponse of(
            UserSummaryResponse user,
            String contents,
            List<String> imgUrls,
            List<NetworkCommentResponse> comments,
            int likeCnt,
            int viewCnt
    ) {
        return new NetworkPostResponse(user, contents, imgUrls, comments, likeCnt, viewCnt);
    }

    public static NetworkPostResponse from(
            Post post,
            UserSummaryResponse user,
            List<String> imgUrls,
            List<NetworkCommentResponse> comments
    ) {
        return of(user, post.getContents(), imgUrls, comments, post.getLikeCnt(), post.getViewCnt());
    }
}
