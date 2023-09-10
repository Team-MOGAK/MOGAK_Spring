package com.mogak.spring.web.controller;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.global.annotation.ExtractUserId;
import com.mogak.spring.service.AwsS3Service;
import com.mogak.spring.service.UserService;
import com.mogak.spring.web.dto.UserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import static com.mogak.spring.web.dto.UserRequestDto.*;
import static com.mogak.spring.web.dto.UserResponseDto.ToCreateDto;

@Tag(name = "유저 API", description = "유저 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private static String dirName = "profile";

    @Operation(summary = "닉네임 검증", description = "PathVariable로 입력받은 닉네임을 검증합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임"),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/{nickname}/verify")
    public ResponseEntity<BaseResponse<ErrorCode>> verifyNickname(@PathVariable String nickname) {
        userService.verifyNickname(nickname);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));

    }

    @Operation(summary = "(임시)계정 생성", description = "계정 생성을 한다",
            responses = {
                    @ApiResponse(responseCode = "201", description = "계정 생성 완료"),
                    @ApiResponse(responseCode = "400", description = "존재하지 않은 직업, 존재하지 않은 주소",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임, 올바르지 않은 이메일",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/join")
    public ResponseEntity<BaseResponse<ToCreateDto>> createUser(@RequestPart CreateUserDto request, @RequestPart(required = false) MultipartFile multipartFile) {
        UploadImageDto uploadImageDto;
        if(multipartFile.isEmpty()) {
            uploadImageDto = UploadImageDto.builder()
                    .imgUrl(null)
                    .imgName(null)
                    .build();
        }
        else {
            uploadImageDto = awsS3Service.uploadProfileImg(multipartFile,dirName);
        }
        User user = userService.create(request,uploadImageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(UserConverter.toCreateDto(user)));
    }

    @Operation(summary = "(임시)로그인", description = "입력한 이메일로 로그인을 시도합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 계정",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 이메일 형식",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/login/{email}")
    public ResponseEntity<BaseResponse<ErrorCode>> login(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok().headers(userService.getHeader(user)).body(new BaseResponse<>(ErrorCode.SUCCESS));
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
    public ResponseEntity<BaseResponse<ErrorCode>> updateNickname(@ExtractUserId Long userId,
                                                                  @RequestBody UpdateNicknameDto nicknameDto, HttpServletRequest req) {
        userService.updateNickname(userId, nicknameDto);
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
    public ResponseEntity<BaseResponse<ErrorCode>> updateJob(@ExtractUserId Long userId,
                                                             @RequestBody UpdateJobDto jobDto) {
        userService.updateJob(userId, jobDto);
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
    public ResponseEntity<Void> updateImage(@ExtractUserId Long userId, @RequestPart MultipartFile multipartFile) {
        String profileImgName = userService.getProfileImgName(userId); //기존 프로필사진받아오기
        UserRequestDto.UpdateImageDto updateImageDto = awsS3Service.updateProfileImg(multipartFile, profileImgName, dirName);
        userService.updateImg(userId, updateImageDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
