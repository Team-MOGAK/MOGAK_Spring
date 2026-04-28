package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import com.mogak.spring.web.dto.commentdto.CommentResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    private PostResponseDto() {
    }

    public record PostDto(
            Long postId,
            Long mogakId,
            Long jogakId,
            Long dailyJogakId,
            LocalDate targetDate,
            Long userId,
            String contents,
            List<String> imgUrls,
            List<Long> commentId,
            int likeCnt,
            int commentCnt
    ) {
        public static PostDto of(
                Long postId,
                Long mogakId,
                Long jogakId,
                Long dailyJogakId,
                LocalDate targetDate,
                Long userId,
                String contents,
                List<String> imgUrls,
                List<Long> commentId,
                int likeCnt,
                int commentCnt
        ) {
            return new PostDto(postId, mogakId, jogakId, dailyJogakId, targetDate, userId, contents, imgUrls, commentId, likeCnt, commentCnt);
        }

        public static PostDto from(Post post, List<String> imgUrls, List<Long> commentIds) {
            DailyJogak dailyJogak = post.getDailyJogak();
            return of(
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
    }

    public record GetPostDto(
            Long postId,
            Long mogakId,
            Long jogakId,
            Long dailyJogakId,
            LocalDate targetDate,
            String contents,
            String thumbnailUrl,
            int likeCnt
    ) {
        public static GetPostDto of(
                Long postId,
                Long mogakId,
                Long jogakId,
                Long dailyJogakId,
                LocalDate targetDate,
                String contents,
                String thumbnailUrl,
                int likeCnt
        ) {
            return new GetPostDto(postId, mogakId, jogakId, dailyJogakId, targetDate, contents, thumbnailUrl, likeCnt);
        }

        public static GetPostDto from(Post post) {
            DailyJogak dailyJogak = post.getDailyJogak();
            return of(
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
    }

    public record PostListDto(
            List<PostResponseDto.GetPostDto> items,
            Integer page,
            Integer size,
            @JsonProperty("hasNext") boolean hasNext
    ) {
        public static PostListDto of(List<PostResponseDto.GetPostDto> items, Integer page, Integer size, boolean hasNext) {
            return new PostListDto(items, page, size, hasNext);
        }
    }

    public record CreatePostDto(
            Long id,
            Long mogakId,
            Long jogakId,
            Long dailyJogakId,
            LocalDate targetDate,
            Long userId,
            String contents,
            List<String> imgUrls,
            LocalDateTime createdAt
    ) {
        public static CreatePostDto of(
                Long id,
                Long mogakId,
                Long jogakId,
                Long dailyJogakId,
                LocalDate targetDate,
                Long userId,
                String contents,
                List<String> imgUrls,
                LocalDateTime createdAt
        ) {
            return new CreatePostDto(id, mogakId, jogakId, dailyJogakId, targetDate, userId, contents, imgUrls, createdAt);
        }

        public static CreatePostDto from(Post post, List<String> imgUrls) {
            DailyJogak dailyJogak = post.getDailyJogak();
            return of(
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
    }

    public record UpdatePostDto(
            Long id,
            String contents,
            LocalDateTime updatedAt
    ) {
        public static UpdatePostDto of(Long id, String contents, LocalDateTime updatedAt) {
            return new UpdatePostDto(id, contents, updatedAt);
        }

        public static UpdatePostDto from(Post post, LocalDateTime updatedAt) {
            return of(post.getId(), post.getContents(), updatedAt);
        }
    }

    public record DeletePostDto(@JsonProperty("deleted") boolean deleted) {
        public static DeletePostDto deletedResponse() {
            return new DeletePostDto(true);
        }
    }

    public record NetworkPostDto(
            UserResponseDto.UserDto user,
            String contents,
            List<String> imgUrls,
            List<CommentResponseDto.NetworkCommentDto> comments,
            int likeCnt,
            int viewCnt
    ) {
        public static NetworkPostDto of(
                UserResponseDto.UserDto user,
                String contents,
                List<String> imgUrls,
                List<CommentResponseDto.NetworkCommentDto> comments,
                int likeCnt,
                int viewCnt
        ) {
            return new NetworkPostDto(user, contents, imgUrls, comments, likeCnt, viewCnt);
        }

        public static NetworkPostDto from(
                Post post,
                UserResponseDto.UserDto user,
                List<String> imgUrls,
                List<CommentResponseDto.NetworkCommentDto> comments
        ) {
            return of(user, post.getContents(), imgUrls, comments, post.getLikeCnt(), post.getViewCnt());
        }
    }

    public record GetAllNetworkDto(
            Long postId,
            String userName,
            String userJob,
            String contents,
            List<String> imgUrls,
            int commentCnt,
            int likeCnt
    ) {
        public static GetAllNetworkDto of(
                Long postId,
                String userName,
                String userJob,
                String contents,
                List<String> imgUrls,
                int commentCnt,
                int likeCnt
        ) {
            return new GetAllNetworkDto(postId, userName, userJob, contents, imgUrls, commentCnt, likeCnt);
        }

        public static GetAllNetworkDto from(Post post, List<String> imgUrls) {
            return of(
                    post.getId(),
                    post.getUser().getNickname(),
                    post.getUser().getJob().getName(),
                    post.getContents(),
                    imgUrls,
                    post.getCommentCnt(),
                    post.getLikeCnt()
            );
        }
    }

    public record NetworkListDto(
            List<PostResponseDto.GetAllNetworkDto> items,
            Integer page,
            Integer size,
            @JsonProperty("hasNext") boolean hasNext
    ) {
        public static NetworkListDto of(List<PostResponseDto.GetAllNetworkDto> items, Integer page, Integer size, boolean hasNext) {
            return new NetworkListDto(items, page, size, hasNext);
        }
    }
}
