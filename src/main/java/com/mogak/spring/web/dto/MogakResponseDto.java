package com.mogak.spring.web.dto;

import lombok.*;

public class MogakResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class createDto {
        private Long mogakId;
        private String title;
    }
}
