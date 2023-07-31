package com.mogak.spring.web.dto;

import lombok.*;

public class UserResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateDto {

    }
}
