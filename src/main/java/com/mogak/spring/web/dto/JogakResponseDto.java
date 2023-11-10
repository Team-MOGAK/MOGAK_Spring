package com.mogak.spring.web.dto;

import lombok.*;

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
        private Long jogakId;
        private String mogakTitle;
        private String category;
        private String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakListDto {
        private int size;
        private List<JogakResponseDto.GetJogakDto> jogaks;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class startDailyJogakDto {
        private Long dailyJogakId;
        private String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class successJogakDto {
        private String title;
        private String mogakTitle;
        private String category;
    }
}
