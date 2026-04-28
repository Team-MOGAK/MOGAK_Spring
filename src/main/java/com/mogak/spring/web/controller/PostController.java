package com.mogak.spring.web.controller;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.service.PostService;
import com.mogak.spring.service.StorageCleanupService;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.result.post.PostListResult;
import com.mogak.spring.service.result.post.PostSummaryResult;
import com.mogak.spring.web.dto.postdto.PostRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDateTime;

import static com.mogak.spring.web.dto.postdto.PostImgRequestDto.CreatePostImgDto;
import static com.mogak.spring.web.dto.postdto.PostResponseDto.*;

@Tag(name = "회고록 API", description = "회고록 API 명세서")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final StorageService storageService;
    private final StorageCleanupService storageCleanupService;
    private static final String DIR_NAME = "img";

    //create
    @Operation(summary = "회고록 생성", description = "회고록을 생성합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 생성"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/api/jogaks/{jogakId}/posts")
    public ResponseEntity<BaseResponse<CreatePostDto>> createPost(@PathVariable Long jogakId,
                                                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                  @Valid @RequestPart PostRequestDto.CreatePostDto request,
                                                                  @RequestPart(required = true) List<MultipartFile> multipartFile) {
        postService.validateCreateAccess(authenticatedUser.getUserId(), request, multipartFile, jogakId);
        List<CreatePostImgDto> postImgDtoList = storageService.uploadImg(multipartFile, DIR_NAME);
        Post post;
        try {
            post = postService.create(authenticatedUser.getUserId(), request, postImgDtoList, jogakId);
        } catch (RuntimeException e) {
            storageCleanupService.deleteUploadedImagesBestEffort(postImgDtoList, DIR_NAME);
            log.warn("Cleaned up uploaded post images after post creation failure. jogakId={}, cause={}",
                    jogakId,
                    e.getClass().getSimpleName());
            throw e;
        }
        List<String> imgUrls = post.getPostImgs().stream()
                .map(PostImg::getImgUrl)
                .toList();
        return ResponseEntity.ok(new BaseResponse<>(CreatePostDto.from(post, imgUrls)));
    }

    //read-전체 조회
    @Operation(summary = "회고록 조회", description = "회고록을 페이징 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "mogakId", description = "모각 ID"),
                    @Parameter(name = "page", description = "페이지 수"),
                    @Parameter(name = "size", description = "페이징 게시물 개수")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/api/mogaks/{mogakId}/posts")
    public ResponseEntity<BaseResponse<PostListDto>> getPostList(@PathVariable Long mogakId,
                                                                 @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "size") int size) {
        PostListResult result = postService.getAllPosts(authenticatedUser.getUserId(), page, mogakId, size);
        PostListDto posts = toPostListDto(result);
        return ResponseEntity.ok(new BaseResponse<>(posts));
    }

    @GetMapping("/api/jogaks/{jogakId}/posts")
    public ResponseEntity<BaseResponse<PostDto>> getPostByJogakAndDate(@PathVariable Long jogakId,
                                                                       @RequestParam("targetDate") java.time.LocalDate targetDate,
                                                                       @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Post post = postService.getByJogakAndTargetDate(authenticatedUser.getUserId(), jogakId, targetDate);
        List<String> imgUrls = postService.findNotThumbnailImg(post);
        List<Long> commentIds = postService.findActiveCommentIds(post);
        return ResponseEntity.ok(new BaseResponse<>(PostDto.from(post, imgUrls, commentIds)));
    }

    //read-상세 조회
    @Operation(summary = "회고록 자세히보기", description = "회고록의 자세한 내용을 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<BaseResponse<PostDto>> getPostDetail(@PathVariable Long postId,
                                                               @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Post post = postService.findById(authenticatedUser.getUserId(), postId);
        List<String> imgUrls = postService.findNotThumbnailImg(post); //썸네일은 제외하고 보여주기
        List<Long> commentIds = postService.findActiveCommentIds(post);
        return ResponseEntity.ok(new BaseResponse<>(PostDto.from(post, imgUrls, commentIds)));
    }

    //update - 권한 설정 필요
    @Operation(summary = "회고록 수정", description = "회고록을 수정합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "350자 제한 초과",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<BaseResponse<UpdatePostDto>> updatePost(@PathVariable Long postId,
                                                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                  @Valid @RequestBody PostRequestDto.UpdatePostDto request) {
        Post post = postService.update(authenticatedUser.getUserId(), postId, request);
        return ResponseEntity.ok(new BaseResponse<>(UpdatePostDto.from(post, LocalDateTime.now())));
    }

    //Delete - 이미지 삭제,댓글 삭제도 구현
    @Operation(summary = "회고록 삭제", description = "회고록을 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<BaseResponse<DeletePostDto>> deletePost(@PathVariable Long postId,
                                                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        postService.delete(authenticatedUser.getUserId(), postId);
        return ResponseEntity.ok(new BaseResponse<>(DeletePostDto.deletedResponse()));
    }

    private GetPostDto toGetPostDto(PostSummaryResult result) {
        return GetPostDto.of(
                result.postId(),
                result.mogakId(),
                result.jogakId(),
                result.dailyJogakId(),
                result.targetDate(),
                result.contents(),
                result.thumbnailUrl(),
                result.likeCnt()
        );
    }

    private PostListDto toPostListDto(PostListResult result) {
        List<GetPostDto> posts = result.items().stream()
                .map(this::toGetPostDto)
                .toList();
        return PostListDto.of(posts, result.page(), result.size(), result.hasNext());
    }

}
