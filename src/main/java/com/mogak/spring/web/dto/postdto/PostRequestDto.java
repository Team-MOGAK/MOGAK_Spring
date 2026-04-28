package com.mogak.spring.web.dto.postdto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PostRequestDto {

    private PostRequestDto() {
    }

    public record CreatePostDto(
            @NotNull LocalDate targetDate,
            @NotNull String contents
    ) {
    }

    public record UpdatePostDto(
            @NotNull String contents
    ) {
    }
}
