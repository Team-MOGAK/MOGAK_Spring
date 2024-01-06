package com.mogak.spring.web.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.service.AuthService;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
import com.mogak.spring.web.dto.authdto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "로그인(auth,token) API", description = "로그인(auth, token, 로그아웃, 회원탈퇴 관련) API 명세서")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "애플로그인을 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "로그인 성공"),})
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AppleLoginResponse>> loginApple(@RequestBody AppleLoginRequest request) {
        AppleLoginResponse response = authService.appleLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }

    /**
     * 토큰 재발급
     */
    @Operation(summary = "토큰 재발급",
            description = "refresh 토큰으로 access&refresh 토큰 재발급을 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "재발급 성공"),})
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<JwtTokens>> refreshToken(@RequestHeader(value = "RefreshToken") String refreshToken) {
        JwtTokens jwtTokens = authService.reissue(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(jwtTokens));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "로그아웃을 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "로그아웃 성공"),})
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<ErrorCode>> logout(@RequestHeader(value = "Authorization") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    /**
     * 회원탈퇴
     */
    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "회원퇄퇴 성공"),})
    @PostMapping("/withdraw")
    public ResponseEntity<BaseResponse<AuthResponse.WithdrawDto>> withdrawUser() {
        AuthResponse.WithdrawDto withdrawDto = authService.deleteUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(withdrawDto));
    }
}
