package com.mogak.spring.web.controller;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.login.AuthHandler;
import com.mogak.spring.service.FollowService;
import com.mogak.spring.web.dto.FollowRequestDto.CountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mogak.spring.web.dto.UserResponseDto.UserDto;

@Tag(name = "팔로우 API", description = "팔로우 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/follows")
public class FollowController {

    private final FollowService followService;
    private final AuthHandler authHandler;

    @Operation(summary = "팔로우", description = "원하는 유저를 팔로우 합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "nickname", description = "팔로우 할 유저 닉네임")
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "팔로우 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 팔로우입니다"),
            })
    @PostMapping("{nickname}")
    public ResponseEntity<BaseResponse<ErrorCode>> follow(@PathVariable String nickname) {
        followService.follow(authHandler.getUserId(), nickname);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    @Operation(summary = "언팔로우", description = "팔로우 했던 유저를 언팔로우 합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "nickname", description = "팔로우 할 유저 닉네임")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "존재하지 않는 팔로우"),
            })
    @DeleteMapping("{nickname}")
    public ResponseEntity<BaseResponse<ErrorCode>> unfollow(@PathVariable String nickname) {
        followService.unfollow(authHandler.getUserId(), nickname);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    @Operation(summary = "팔로우,팔로잉 숫자 조회", description = "모토, 멘토 숫자를 조회합니다",
            parameters = {
                    @Parameter(name = "nickname", description = "유저 닉네임")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "모토, 멘토수 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류"),
            })
    @GetMapping("counts/{nickname}")
    public ResponseEntity<BaseResponse<CountDto>> getFollowCount(@PathVariable String nickname) {
        return ResponseEntity.ok(new BaseResponse<>(followService.getFollowCount(nickname)));
    }

    @Operation(summary = "모토 조회", description = "유저를 팔로우 중인 모토들을 조회 합니다",
            parameters = {
                    @Parameter(name = "nickname", description = "유저 닉네임")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "모토 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류"),
            })
    @GetMapping("{nickname}/motos")
    public ResponseEntity<BaseResponse<List<UserDto>>> getMotoList(@PathVariable String nickname) {
        return ResponseEntity.ok(new BaseResponse<>(followService.getMotoList(nickname)));
    }

    @Operation(summary = "멘토 조회", description = "유저가 팔로우 중인 멘토들을 조회 합니다",
            parameters = {
                    @Parameter(name = "nickname", description = "유저 닉네임")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "멘토 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류"),
            })
    @GetMapping("{nickname}/mentors")
    public ResponseEntity<BaseResponse<List<UserDto>>> getMentorList(@PathVariable String nickname) {
        return ResponseEntity.ok(new BaseResponse<>(followService.getMentorList(nickname)));
    }
    
}
