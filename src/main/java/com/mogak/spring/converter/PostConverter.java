package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostConverter {

    //이미지까지 업로드 잘 되어있는지 확인
    public static PostResponseDto.CreatePostDto toCreatePostDto(Post post){
        DailyJogak dailyJogak = post.getDailyJogak();
        List<String> imgUrls = post.getPostImgs().stream()
                .map(PostImg::getImgUrl)
                .collect(Collectors.toList());
        return PostResponseDto.CreatePostDto.of(
                post.getId(),
                dailyJogak.getJogak().getMogak().getId(),
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                post.getUser().getId(),
                post.getContents(),
                imgUrls,
                post.getCreatedAt()
        );
    }

    public static Post toPost(PostRequestDto.CreatePostDto request, User user, DailyJogak dailyJogak){
        return Post.builder()
                .dailyJogak(dailyJogak)
                .user(user)
                .contents(request.contents())
                .viewCnt(0)  //조회수 초기화
                .build();
    }
    //상세 조회
    public static PostResponseDto.PostDto toPostDto(Post post, List<String> imgUrls, List<Long> commentIds){
        DailyJogak dailyJogak = post.getDailyJogak();
        return PostResponseDto.PostDto.of(
                post.getId(),
                dailyJogak.getJogak().getMogak().getId(),
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                post.getUser().getId(),
                post.getContents(),
                imgUrls,
                commentIds,
                post.getLikeCnt(),
                post.getCommentCnt()
        );
    }

    public static PostResponseDto.UpdatePostDto toUpdatePostDto(Post post){
        return PostResponseDto.UpdatePostDto.of(post.getId(), post.getContents(), LocalDateTime.now());
    }

    public static PostResponseDto.DeletePostDto toDeletePostDto(){
        return PostResponseDto.DeletePostDto.deletedResponse();
    }
    //전체조회
    public static PostResponseDto.GetPostDto toGetPostDto(Post post){
        DailyJogak dailyJogak = post.getDailyJogak();
        return PostResponseDto.GetPostDto.of(
                post.getId(),
                dailyJogak.getJogak().getMogak().getId(),
                dailyJogak.getJogak().getId(),
                dailyJogak.getId(),
                dailyJogak.getTargetDate(),
                post.getContents(),
                post.getPostThumbnailUrl(),
                post.getLikeCnt()
        );
    }
    public static List<PostResponseDto.GetPostDto> toPostDtoList(List<Post> postList){
        return postList.stream()
                .map(post -> toGetPostDto(post))
                .collect(Collectors.toList());
    }
    public static PostResponseDto.PostListDto toPostListDto(List<Post> postList){
        return PostResponseDto.PostListDto.of(toPostDtoList(postList), false, postList.size());
    }
    public static Slice<PostResponseDto.GetPostDto> toPostPagingDto(Slice<Post> posts){
        return posts.map(post -> toGetPostDto(post));
    }
    //네트워킹전체조회
    public static PostResponseDto.GetAllNetworkDto toGetNetworkDto(Post post){
        List<String> imgUrls = post.getPostImgs().stream()
                .filter(img -> !Objects.equals(img.getImgUrl(), post.getPostThumbnailUrl()))
                .map(PostImg::getImgUrl)
                .collect(Collectors.toList());
        return PostResponseDto.GetAllNetworkDto.of(
                post.getId(),
                post.getUser().getNickname(),
                post.getUser().getJob().getName(),
                post.getContents(),
                imgUrls,
                post.getCommentCnt(),
                post.getLikeCnt()
        );

    }
    public static List<PostResponseDto.GetAllNetworkDto> toNetworkDtoList(List<Post> postList){
        return postList.stream()
                .map(post -> toGetNetworkDto(post))
                .collect(Collectors.toList());
    }
    public static PostResponseDto.NetworkListDto toNetworkListDto (List<Post> postList){
        return PostResponseDto.NetworkListDto.of(toNetworkDtoList(postList), false, postList.size());
    }

    public static Slice<PostResponseDto.GetAllNetworkDto> toNetworkPagingDto(Slice<Post> posts){
        return posts.map(post -> toGetNetworkDto(post));
    }

    //좋아요 생성
    public static PostResponseDto.PostDto toCreateLikePostDto(Post post){
        return PostResponseDto.PostDto.of(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                post.getLikeCnt(),
                0
        );
    }
}
