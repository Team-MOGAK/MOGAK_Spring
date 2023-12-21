package com.mogak.spring.web.dto.mogakdto;

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
        private Long id;
        private String title;
        private String state;
        private MogakCategory bigCategory;
        private String smallCategory;
        private String color;
        private LocalDate startAt;
        private LocalDate endAt;
    }
}