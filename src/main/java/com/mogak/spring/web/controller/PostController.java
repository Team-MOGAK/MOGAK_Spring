package com.mogak.spring.web.controller;

import com.mogak.spring.converter.PostConverter;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.service.AwsS3Service;
import com.mogak.spring.service.PostImgService;
import com.mogak.spring.service.PostService;
import com.mogak.spring.web.dto.PostImgRequestDto;
import com.mogak.spring.web.dto.PostRequestDto;
import com.mogak.spring.web.dto.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "회고록 API", description = "회고록 API 명세서")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final AwsS3Service awsS3Service;
    private final PostImgService postImgService;
    private static String dirName = "img";

    //create
    @Operation(summary = "회고록 생성", description = "회고록을 생성합니다",
            parameters = @Parameter(name = "mogakId", description = "모각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 생성"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/mogaks/{mogakId}/posts")
    public ResponseEntity<PostResponseDto.CreatePostDto> createPost(@PathVariable Long mogakId,
                                                                    @RequestPart PostRequestDto.CreatePostDto request,
                                                                    @RequestPart(required = true) List<MultipartFile> multipartFile/*User user*/){
        //에러핸들링 필요
        List<PostImgRequestDto.CreatePostImgDto> postImgDtoList = awsS3Service.uploadImg(multipartFile, dirName);
        Post post = postService.create(request, postImgDtoList, mogakId);
        return ResponseEntity.ok(PostConverter.toCreatePostDto(post));
    }

    //read-전체 조회
    @Operation(summary = "회고록 조회", description = "회고록을 페이징 조회합니다",
            parameters = @Parameter(name = "mogakId", description = "모각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/mogaks/{mogakId}/posts")
    public ResponseEntity<Slice<PostResponseDto.GetPostDto>> getPostList(@PathVariable Long mogakId,
                                                                         @RequestParam(value = "lastPostId") Long lastPostId,
                                                                         @RequestParam(value = "size") int size){
        Slice<Post> posts = postService.getAllPosts(lastPostId, mogakId, size);
        //다음페이지 존재 여부 전달 필요
        return ResponseEntity.ok(PostConverter.toPostPagingDto(posts));
    }

    //read-상세 조회
    @Operation(summary = "회고록 자세히보기", description = "회고록의 자세한 내용을 조회합니다",
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.PostDto> getPostDetail(@PathVariable Long postId) {
        Post post= postService.findById(postId);
        List<String> imgUrls = postImgService.findNotThumbnailImg(post);
        return ResponseEntity.ok(PostConverter.toPostDto(post, imgUrls));
    }

    //update - 권한 설정 필요
    @Operation(summary = "회고록 수정", description = "회고록을 수정합니다",
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "350자 제한 초과",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.UpdatePostDto> updatePost(@PathVariable Long postId,
                                                                    @RequestBody PostRequestDto.UpdatePostDto request){
        Post post = postService.update(postId, request);
        return ResponseEntity.ok(PostConverter.toUpdatePostDto(post));
    }

    //Delete - s3 이미지 삭제,댓글 삭제도 구현
    @Operation(summary = "회고록 삭제", description = "회고록을 삭제합니다",
            parameters = @Parameter(name = "postId", description = "게시물 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회고록 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회고록",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/mogaks/posts/{postId}")
    public ResponseEntity<PostResponseDto.DeletePostDto> deletePost(@PathVariable Long postId) {
        Post post = postService.findById(postId);
        List<PostImg> postImgList = postImgService.findAllByPost(post);
        awsS3Service.deleteImg(postImgList,dirName); //s3이미지 객체 삭제
        postService.delete(postId);
        return ResponseEntity.ok(PostConverter.toDeletePostDto());
    }

}
