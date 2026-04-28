package com.mogak.spring.web.dto.commentdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentDeleteResponse(@JsonProperty("deleted") boolean deleted) {
    public static CommentDeleteResponse deletedResponse() {
        return new CommentDeleteResponse(true);
    }
}
