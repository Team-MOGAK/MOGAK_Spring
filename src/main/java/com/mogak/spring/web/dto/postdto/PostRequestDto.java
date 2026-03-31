package com.mogak.spring.web.dto.postdto;

import lombok.Getter;

public class PostRequestDto {

    @Getter
    public static class CreatePostDto{
        private String contents;
    }



    @Getter
    public static class UpdatePostDto{
        public String contents;
    }


}
