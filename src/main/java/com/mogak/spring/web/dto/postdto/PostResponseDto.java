package com.mogak.spring.web.dto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    }

    public record PostListDto(
            List<PostResponseDto.GetPostDto> postDtoList,
            @JsonProperty("hasNext") boolean hasNext,
            Integer size
    ) {
        public static PostListDto of(List<PostResponseDto.GetPostDto> postDtoList, boolean hasNext, Integer size) {
            return new PostListDto(postDtoList, hasNext, size);
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
    }

    public record UpdatePostDto(
            Long id,
            String contents,
            LocalDateTime updatedAt
    ) {
        public static UpdatePostDto of(Long id, String contents, LocalDateTime updatedAt) {
            return new UpdatePostDto(id, contents, updatedAt);
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
    }

    public record NetworkListDto(
            List<PostResponseDto.GetAllNetworkDto> postDtoList,
            @JsonProperty("hasNext") boolean hasNext,
            Integer size
    ) {
        public static NetworkListDto of(List<PostResponseDto.GetAllNetworkDto> postDtoList, boolean hasNext, Integer size) {
            return new NetworkListDto(postDtoList, hasNext, size);
        }
    }
}
