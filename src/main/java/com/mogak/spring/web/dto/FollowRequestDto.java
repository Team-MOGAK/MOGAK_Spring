package com.mogak.spring.web.dto;

import lombok.*;

public class FollowRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CountDto {
        private int mentorCnt;
        private int motoCnt;
    }
}
