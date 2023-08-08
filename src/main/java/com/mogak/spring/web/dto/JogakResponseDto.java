package com.mogak.spring.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class JogakResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateJogakDto {
        private LocalDateTime startTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakDto {
        private String mogakTitle;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakListDto {
        private List<JogakResponseDto.GetJogakDto> jogaks;
        private int size;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class startJogakDto {
        private String title;
        private LocalDateTime startTime;
    }
}
