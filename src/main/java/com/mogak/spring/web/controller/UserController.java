package com.mogak.spring.web.controller;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.CommonException;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.service.UserService;
import com.mogak.spring.web.dto.UserResponseDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.mogak.spring.web.dto.UserRequestDto.*;

@Tag(name = "유저 API", description = "유저 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /**
     * 닉네임 검증 API
     */
    @Operation(summary = "닉네임 검증", description = "PathVariable로 입력받은 닉네임을 검증합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임"),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/{nickname}/verify")
    public ResponseEntity<BaseResponse<ErrorCode>> verifyNickname(@PathVariable String nickname) {
        if (userService.verifyNickname(nickname) && userService.findUserByNickname(nickname)) {
            throw new CommonException(ErrorCode.NOT_VALID_NICKNAME);
        } else {
            return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
        }
    }

    /**
     * 임시 계정 생성 API
     */
    @Operation(summary = "(임시)계정 생성", description = "계정 생성을 한다",
            responses = {
                    @ApiResponse(responseCode = "201", description = "계정 생성 완료"),
                    @ApiResponse(responseCode = "400", description = "존재하지 않은 직업, 존재하지 않은 주소",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 닉네임, 올바르지 않은 이메일",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/join")
    public ResponseEntity<UserResponseDto.ToCreateDto> createUser(@RequestBody CreateUserDto request) {
        User user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserConverter.toCreateDto(user));
    }

    /**
     * 임시 계정 로그인 API
     */
    @Operation(summary = "로그인", description = "입력한 이메일로 로그인을 시도합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 계정",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "올바르지 않은 이메일 형식",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/login/{email}")
    public ResponseEntity<UserResponseDto.LoginDto> login(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok().headers(userService.getHeader(user)).build();
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
    public ResponseEntity<BaseResponse<ErrorCode>> updateNickname(@RequestBody UpdateNicknameDto nicknameDto, HttpServletRequest req) {
        userService.updateNickname(nicknameDto, req);
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
    public ResponseEntity<BaseResponse<ErrorCode>> updateJob(@RequestBody UpdateJobDto jobDto, HttpServletRequest req) {
        userService.updateJob(jobDto, req);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

//    @PutMapping("/profile/image")
//    public ResponseEntity<Void> updateImage(@RequestBody UpdateImageDto imgDto, HttpServletRequest req) {
//        userService.updateImg(imgDto, req);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

}
