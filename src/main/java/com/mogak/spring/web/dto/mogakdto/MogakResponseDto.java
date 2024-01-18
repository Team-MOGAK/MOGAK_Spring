package com.mogak.spring.web.dto.mogakdto;

import com.mogak.spring.domain.mogak.MogakCategory;
import lombok.*;
import java.util.List;

public class MogakResponseDto {

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
        private MogakCategory bigCategory;
        private String smallCategory;
        private String color;
    }
}