package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import com.mogak.spring.web.dto.postdto.PostResponseDto;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {

    //이미지까지 업로드 잘 되어있는지 확인
    public static PostResponseDto.CreatePostDto toCreatePostDto(Post post){
        return PostResponseDto.CreatePostDto.builder()
                .id(post.getId())
                .mogakId(post.getMogak().getId())
                .userId(post.getUser().getId())
                .contents(post.getContents())
                .validation(Validation.ACTIVE.toString())
                .createdAt(post.getCreatedAt())
                .imgUrls(post.getPostImgs().stream()
                        .map(m -> m.getImgUrl())
                        .collect(Collectors.toList())
                )
                .build();
    }

    public static Post toPost(PostRequestDto.CreatePostDto request, User user, Mogak mogak){
        return Post.builder()
                .mogak(mogak)
                .user(user)
                .contents(request.getContents())
                .viewCnt(0)  //조회수 초기화
                .validation(Validation.ACTIVE.toString())
                .build();
    }
    //상세 조회
    public static PostResponseDto.PostDto toPostDto(Post post, List<String> imgUrls){
        return PostResponseDto.PostDto.builder()
                .postId(post.getId())
                .mogakId(post.getMogak().getId())
                .userId(post.getUser().getId())
                .contents(post.getContents())
                .imgUrls(imgUrls)
                .commentId(post.getPostComments().stream()
                        .map(m -> m.getId())
                        .collect(Collectors.toList())) //일단 comment id로 조회하는 것으로 함
                .likeCnt(post.getLikeCnt())
                .commentCnt(post.getCommentCnt())
                .build();
    }

    public static PostResponseDto.UpdatePostDto toUpdatePostDto(Post post){
        return PostResponseDto.UpdatePostDto.builder()
                .id(post.getId())
                .contents(post.getContents())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PostResponseDto.DeletePostDto toDeletePostDto(){
        return PostResponseDto.DeletePostDto.builder()
                .validation(Validation.INACTIVE.toString())
                .build();
    }
    //전체조회
    public static PostResponseDto.GetPostDto toGetPostDto(Post post){
        return PostResponseDto.GetPostDto.builder()
                .postId(post.getId())
                .mogakId(post.getMogak().getId())
                .contents(post.getContents())
                .thumbnailUrl(post.getPostThumbnailUrl())
                .build();
    }
    public static List<PostResponseDto.GetPostDto> toPostDtoList(List<Post> postList){
        return postList.stream()
                .map(post -> toGetPostDto(post))
                .collect(Collectors.toList());
    }
    public static PostResponseDto.PostListDto toPostListDto(List<Post> postList){
        return PostResponseDto.PostListDto.builder()
                .postDtoList(toPostDtoList(postList))
                .size(postList.size())
                .build();
    }
    public static Slice<PostResponseDto.GetPostDto> toPostPagingDto(Slice<Post> posts){
        return posts.map(post -> toGetPostDto(post));
    }
    //네트워킹전체조회
    public static PostResponseDto.GetAllNetworkDto toGetNetworkDto(Post post){
        return PostResponseDto.GetAllNetworkDto.builder()
                .postId(post.getId())
                .userName(post.getUser().getNickname())
                .userJob(post.getUser().getJob().getName())
                .contents(post.getContents())
                .imgUrls(post.getPostImgs().stream()
                        .filter(img -> img.getImgUrl() != post.getPostThumbnailUrl())
                        .map(PostImg::getImgUrl)
                        .collect(Collectors.toList()))
                .commentCnt(post.getCommentCnt())
                .likeCnt(post.getLikeCnt())
                .build();

    }
    public static List<PostResponseDto.GetAllNetworkDto> toNetworkDtoList(List<Post> postList){
        return postList.stream()
                .map(post -> toGetNetworkDto(post))
                .collect(Collectors.toList());
    }
    public static PostResponseDto.NetworkListDto toNetworkListDto (List<Post> postList){
        return PostResponseDto.NetworkListDto.builder()
                .postDtoList(toNetworkDtoList(postList))
                .size(postList.size())
                .build();
    }

    public static Slice<PostResponseDto.GetAllNetworkDto> toNetworkPagingDto(Slice<Post> posts){
        return posts.map(post -> toGetNetworkDto(post));
    }

    //좋아요 생성
    public static PostResponseDto.PostDto toCreateLikePostDto(Post post){
        return PostResponseDto.PostDto.builder()
                .likeCnt(post.getLikeCnt())
                .build();
    }
}
