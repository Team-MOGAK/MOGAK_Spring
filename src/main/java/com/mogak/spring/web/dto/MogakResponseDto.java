package com.mogak.spring.web.dto;

import lombok.*;

import java.time.LocalDateTime;

public class MogakResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class createDto {
        private Long mogakId;
        private String title;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class updateStateDto {
        private Long mogakId;
        private LocalDateTime updatedAt;
    }
}
