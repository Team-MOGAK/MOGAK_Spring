package com.mogak.spring.web.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthWithdrawResponse(
        @JsonProperty("isDeleted") boolean isDeleted
) {
}
