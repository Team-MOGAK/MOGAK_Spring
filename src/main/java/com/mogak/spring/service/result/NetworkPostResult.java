package com.mogak.spring.service.result;

import java.util.List;

public record NetworkPostResult(
        UserSummaryResult user,
        String contents,
        List<String> imgUrls,
        List<NetworkCommentResult> comments,
        int likeCnt,
        int viewCnt
) {
}
