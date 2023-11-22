package com.mogak.spring.converter;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.web.dto.postdto.PostImgRequestDto;

public class PostImgConverter {
    /*
    public static PostImgResponseDto.CreatePostImgDto toCreatePostImgDto(PostImg post){
        return

    }
*/
    public static PostImg toPostImg(PostImgRequestDto.CreatePostImgDto request, Post post){
        return PostImg.builder()
                .post(post)
                .imgName(request.getImgName())
                .imgUrl(request.getImgUrl())
                .build();
    }
}
