package com.mogak.spring.web.dto;

import com.mogak.spring.domain.jogak.Jogak;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

public class JogakResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateJogakDto {
        private LocalTime startTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakDto {
        private String mogakTitle;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetJogakListDto {
        private List<JogakResponseDto.GetJogakDto> jogaks;
        private int size;
    }
}
