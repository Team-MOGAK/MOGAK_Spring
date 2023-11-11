package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.web.dto.userdto.UserResponseDto;
import com.mogak.spring.web.dto.commentdto.CommentResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {

    //read-상세조회
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostDto {
        private Long postId;
        private Long mogakId;
        private Long userId; //추후 로그인 구현후 수정 필요할듯
        private String contents;
        private List<String> imgUrls;
        private List<Long> commentId;
        private int likeCnt;
        private int commentCnt;

    }
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    //리스트를 위한 post dto
    public static class GetPostDto {
        private Long postId;
        private Long mogakId;
        private String contents;
        private String thumbnailUrl;
        private int likeCnt;
    }
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostListDto {
        private List<PostResponseDto.GetPostDto> postDtoList;
        private boolean hasNext; //다음페이지 존재하는지의 여부 + 추가 구현 필요
        private Integer size;
    }
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreatePostDto {
        private Long id;
        private Long mogakId;
        private Long userId;
        private String contents;
        private String validation;
        private List<String> imgUrls;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdatePostDto {
        private Long id;
        private String contents;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeletePostDto {
        private String validation;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NetworkPostDto {
        private UserResponseDto.UserDto user;
        private String contents;
        private List<String> imgUrls;
        private List<CommentResponseDto.NetworkCommentDto> comments;
        private int likeCnt;
        private int viewCnt;
    }

    //전체 네트워크
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetAllNetworkDto {
        private Long postId;
        private String userName;
        private String userJob;
        private String contents;
        private List<String> imgUrls;
        private int commentCnt;
        private int likeCnt;
    }
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NetworkListDto {
        private List<PostResponseDto.GetAllNetworkDto> postDtoList;
        private boolean hasNext; //다음페이지 존재하는지의 여부 + 추가 구현 필요
        private Integer size;
    }

}
