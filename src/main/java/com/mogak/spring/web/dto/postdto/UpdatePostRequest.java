package com.mogak.spring.web.dto.postdto;

import jakarta.validation.constraints.NotNull;

public record UpdatePostRequest(
        @NotNull String contents
) {
}
