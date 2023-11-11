package com.mogak.spring.web.dto.postdto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;



public class PostImgRequestDto {

    @Getter
    @Builder
    @ToString
    public static class CreatePostImgDto{
        private String imgName;
        private String imgUrl;
        private boolean thumbnail;
    }
}
