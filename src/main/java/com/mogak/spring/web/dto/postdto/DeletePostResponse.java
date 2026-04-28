package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeletePostResponse(@JsonProperty("deleted") boolean deleted) {
    public static DeletePostResponse deletedResponse() {
        return new DeletePostResponse(true);
    }
}
