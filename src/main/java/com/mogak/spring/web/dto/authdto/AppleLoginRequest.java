package com.mogak.spring.web.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleLoginRequest(
        @JsonProperty("id_token") String idToken
) {
}
