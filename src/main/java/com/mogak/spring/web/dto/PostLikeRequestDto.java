package com.mogak.spring.web.dto;

import lombok.Getter;

public class PostLikeRequestDto {

    @Getter
    public static class CreateLikeDto{
        private Long userId;
    }
}