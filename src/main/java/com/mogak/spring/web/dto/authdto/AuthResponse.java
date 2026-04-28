package com.mogak.spring.web.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {

    public record WithdrawDto(
            @JsonProperty("isDeleted") boolean isDeleted
    ) {
    }
}
