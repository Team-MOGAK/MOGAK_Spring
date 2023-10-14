package com.mogak.spring.web.dto.ModaratDto;

import com.mogak.spring.domain.user.User;
import lombok.*;

public class ModaratResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateModaratDto {
        private User user;
        private String title;
        private String color;
    }
}
