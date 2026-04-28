package com.mogak.spring.web.controller;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.service.StorageService;
import com.mogak.spring.service.UserService;
import com.mogak.spring.service.result.ProfileImageResult;
import com.mogak.spring.service.result.UserCreateResult;
import com.mogak.spring.service.result.UserProfileResult;
import com.mogak.spring.web.dto.userdto.UserCreateRequest;
import com.mogak.spring.web.dto.userdto.UserCreateResponse;
import com.mogak.spring.web.dto.userdto.UserGetEmailRequest;
import com.mogak.spring.web.dto.userdto.UserNicknameCheckRequest;
import com.mogak.spring.web.dto.userdto.UserProfileResponse;
import com.mogak.spring.web.dto.userdto.UserUpdateJobRequest;
import com.mogak.spring.web.dto.userdto.UserUpdateNicknameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Tag(name = "유저 API", description = "유저 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final StorageService storageService;
    private static String dirName = "profile";

    @Operation(summary = "닉네임 검증", description = "PathVariable로 입력받은 닉네임을 검증합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임"),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/nickname/verify")
    public ResponseEntity<BaseResponse<ErrorCode>> verifyNickname(@Valid @RequestBody UserNicknameCheckRequest request) {
        userService.verifyNickname(request.nickname());
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));

    }

    @Operation(summary = "회원등록", description = "회원 등록을 합니다",
            responses = {
                    @ApiResponse(responseCode = "201", description = "계정 생성 완료"),
                    @ApiResponse(responseCode = "400", description = "존재하지 않은 직업, 존재하지 않은 주소",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임, 올바르지 않은 이메일",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<BaseResponse<UserCreateResponse>> createUser(@Valid @RequestPart UserCreateRequest request,
                                                                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                              @RequestPart(required = false) MultipartFile multipartFile) {
        ProfileImageResult profileImage;
        if (multipartFile == null || multipartFile.isEmpty()) {
            profileImage = new ProfileImageResult(null, null);
        } else {
            profileImage = storageService.uploadProfileImg(multipartFile, dirName);
        }
        UserCreateResult result = userService.create(
                authenticatedUser.getUserId(),
                request.nickname(),
                request.job(),
                request.address(),
                profileImage
        );
        UserCreateResponse response = new UserCreateResponse(result.userId(), result.nickname(), result.tokens());
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(response));
    }

    @Operation(summary = "임시 로그인", description = "입력한 이메일로 로그인을 시도합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 계정",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 이메일 형식",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody UserGetEmailRequest emailRequest) {
        User user = userService.getUserByEmail(emailRequest.email());
        return ResponseEntity.ok().body(new BaseResponse<>(userService.getToken(user)));
    }

    @Operation(summary = "프로필 조회", description = "유저의 프로필을 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getUserProfile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        UserProfileResult result = userService.getUserProfile(authenticatedUser.getUserId());
        UserProfileResponse response = new UserProfileResponse(result.nickname(), result.job(), result.imgUrl());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }


    @Operation(summary = "닉네임 변경", description = "유저의 닉네임을 변경합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임 형식",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/profile/nickname")
    public ResponseEntity<BaseResponse<ErrorCode>> updateNickname(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                   @Valid @RequestBody UserUpdateNicknameRequest nicknameRequest) {
        userService.updateNickname(authenticatedUser.getUserId(), nicknameRequest.nickname());
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    @Operation(summary = "직무 변경", description = "유저의 직무를 변경합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "직무 변경 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저, 존재하지 않는 직업",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/profile/job")
    public ResponseEntity<BaseResponse<ErrorCode>> updateJob(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                             @Valid @RequestBody UserUpdateJobRequest jobRequest) {
        userService.updateJob(authenticatedUser.getUserId(), jobRequest.job());
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    //프로필 이미지 변경
    @Operation(summary = "프로필사진 변경", description = "유저의 프로필 사진을 변경합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 사진 변경 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/profile/image")
    public ResponseEntity<BaseResponse<ErrorCode>> updateImage(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                               @RequestPart MultipartFile multipartFile) {
        String profileImgName = userService.getProfileImgName(authenticatedUser.getUserId()); //기존 프로필사진받아오기
        ProfileImageResult profileImage;
        if (multipartFile == null || multipartFile.isEmpty()) {
            if (profileImgName != null) {
                storageService.deleteProfileImg(profileImgName);
            }
            profileImage = new ProfileImageResult(null, null);
        } else {
            profileImage = storageService.updateProfileImg(multipartFile, profileImgName, dirName);
        }
        userService.updateImg(authenticatedUser.getUserId(), profileImage);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

}
