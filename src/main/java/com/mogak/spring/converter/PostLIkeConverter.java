package com.mogak.spring.converter;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostLike;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.postdto.PostLikeResponseDto;

public class PostLIkeConverter {

    public static PostLikeResponseDto.CreatePostLikeDto toCreatePostLikeDto(PostLike postLike){
        return PostLikeResponseDto.CreatePostLikeDto.builder()
                .postId(postLike.getPost().getId())
                .userId(postLike.getUser().getId())
                .build();
    }

    public static PostLike toPostLike(Post post, User user){
        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }
}
