package com.mogak.spring.web.dto.userdto;

import jakarta.validation.constraints.Size;

public record UserUpdateJobRequest(
        @Size(min = 1, max = 100)
        String job
) {
}
