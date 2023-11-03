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
        private Long jogakId;
        private String category;
        private String title;
        private Boolean isRoutine;
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

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class endJogakDto {
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
