package com.mogak.spring.web.dto.postdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import com.mogak.spring.web.dto.commentdto.CommentResponseDto;
import lombok.*;

import java.time.LocalDate;
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
        private Long jogakId;
        private Long dailyJogakId;
        private LocalDate targetDate;
        private Long userId; //추후 로그인 구현후 수정 필요할듯
        private String contents;
        private List<String> imgUrls;
        private List<Long> commentId;
        private int likeCnt;
        private int commentCnt;

        public static PostDto from(Post post, List<String> imgUrls, List<Long> commentIds) {
            DailyJogak dailyJogak = post.getDailyJogak();
            return PostDto.builder()
                    .postId(post.getId())
                    .mogakId(dailyJogak.getJogak().getMogak().getId())
                    .jogakId(dailyJogak.getJogak().getId())
                    .dailyJogakId(dailyJogak.getId())
                    .targetDate(dailyJogak.getTargetDate())
                    .userId(post.getUser().getId())
                    .contents(post.getContents())
                    .imgUrls(imgUrls)
                    .commentId(commentIds)
                    .likeCnt(post.getLikeCnt())
                    .commentCnt(post.getCommentCnt())
                    .build();
        }

    }
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    //리스트를 위한 post dto
    public static class GetPostDto {
        private Long postId;
        private Long mogakId;
        private Long jogakId;
        private Long dailyJogakId;
        private LocalDate targetDate;
        private String contents;
        private String thumbnailUrl;
        private int likeCnt;

        public static GetPostDto from(Post post) {
            DailyJogak dailyJogak = post.getDailyJogak();
            return GetPostDto.builder()
                    .postId(post.getId())
                    .mogakId(dailyJogak.getJogak().getMogak().getId())
                    .jogakId(dailyJogak.getJogak().getId())
                    .dailyJogakId(dailyJogak.getId())
                    .targetDate(dailyJogak.getTargetDate())
                    .contents(post.getContents())
                    .thumbnailUrl(post.getPostThumbnailUrl())
                    .build();
        }
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
        private Long jogakId;
        private Long dailyJogakId;
        private LocalDate targetDate;
        private Long userId;
        private String contents;
        private List<String> imgUrls;
        private LocalDateTime createdAt;

        public static CreatePostDto from(Post post, List<String> imgUrls) {
            DailyJogak dailyJogak = post.getDailyJogak();
            return CreatePostDto.builder()
                    .id(post.getId())
                    .mogakId(dailyJogak.getJogak().getMogak().getId())
                    .jogakId(dailyJogak.getJogak().getId())
                    .dailyJogakId(dailyJogak.getId())
                    .targetDate(dailyJogak.getTargetDate())
                    .userId(post.getUser().getId())
                    .contents(post.getContents())
                    .imgUrls(imgUrls)
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdatePostDto {
        private Long id;
        private String contents;
        private LocalDateTime updatedAt;

        public static UpdatePostDto from(Post post, LocalDateTime updatedAt) {
            return UpdatePostDto.builder()
                    .id(post.getId())
                    .contents(post.getContents())
                    .updatedAt(updatedAt)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeletePostDto {
        private boolean deleted;

        public static DeletePostDto deleted() {
            return DeletePostDto.builder()
                    .deleted(true)
                    .build();
        }
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

        public static NetworkPostDto from(Post post,
                                          UserResponseDto.UserDto user,
                                          List<String> imgUrls,
                                          List<CommentResponseDto.NetworkCommentDto> comments) {
            return NetworkPostDto.builder()
                    .user(user)
                    .contents(post.getContents())
                    .imgUrls(imgUrls)
                    .comments(comments)
                    .likeCnt(post.getLikeCnt())
                    .viewCnt(post.getViewCnt())
                    .build();
        }
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

        public static GetAllNetworkDto from(Post post, List<String> imgUrls) {
            return GetAllNetworkDto.builder()
                    .postId(post.getId())
                    .userName(post.getUser().getNickname())
                    .userJob(post.getUser().getJob().getName())
                    .contents(post.getContents())
                    .imgUrls(imgUrls)
                    .commentCnt(post.getCommentCnt())
                    .likeCnt(post.getLikeCnt())
                    .build();
        }
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
