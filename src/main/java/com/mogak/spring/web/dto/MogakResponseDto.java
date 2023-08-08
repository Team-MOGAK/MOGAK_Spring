package com.mogak.spring.web.dto;

import com.mogak.spring.domain.mogak.MogakCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MogakResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDto {
        private Long mogakId;
        private String title;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateStateDto {
        private Long mogakId;
        private LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetMogakListDto {
        private List<GetMogakDto> mogaks;
        private int size;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetMogakDto {
        private String title;
        private String state;
        private List<String> periods;
        private MogakCategory mogakCategory;
        private String otherCategory;
        private LocalDate startAt;
        private LocalDate endAt;
    }
}
