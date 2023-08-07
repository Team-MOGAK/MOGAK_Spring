package com.mogak.spring.web.dto;

import lombok.*;

import java.time.LocalTime;

public class JogakResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JogakDto {
        private LocalTime startTime;
    }

}
