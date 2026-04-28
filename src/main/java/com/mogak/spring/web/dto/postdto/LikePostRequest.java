package com.mogak.spring.web.dto.postdto;

import jakarta.validation.constraints.NotNull;

public record LikePostRequest(@NotNull Long postId) {
}
