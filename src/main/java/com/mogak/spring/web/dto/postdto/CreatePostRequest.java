package com.mogak.spring.web.dto.postdto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePostRequest(
        @NotNull LocalDate targetDate,
        @NotNull String contents
) {
}
