package com.mogak.spring.web.controller;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.service.AuthService;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
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

/*
로그인 테스트를 위함. 이부분 테스트 끝나면 user controller로 옮길 예정
 */
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
    public ResponseEntity<AppleLoginResponse> loginApple(@RequestBody AppleLoginRequest request) {
        AppleLoginResponse response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 재발급
     */
    @Operation(summary = "토큰 재발급",
            description = "refresh 토큰으로 access&refresh 토큰 재발급을 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "재발급 성공"),})
    @PostMapping("/refresh")
    public ResponseEntity<JwtTokens> refreshToken(@RequestHeader(value = "RefreshToken") String refreshToken) {
        JwtTokens jwtTokens = authService.reissue(refreshToken);
        return ResponseEntity.ok(jwtTokens);
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "로그아웃을 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "로그아웃 성공"),})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원탈퇴
     */
    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "회원퇄퇴 성공"),})
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawUser(/*수정*/@RequestBody Long userId) {
        boolean isDeleted = authService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
