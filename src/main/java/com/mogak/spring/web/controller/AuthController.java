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
@Tag(name = "로그인(auth,token) API", description = "로그인(auth, token) API 명세서")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "애플로그인을 합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
            })
    @PostMapping("/login")
    public ResponseEntity<AppleLoginResponse> loginApple(@RequestBody AppleLoginRequest request) {
        AppleLoginResponse response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }

    /*
    @PostMapping("/signup")
    public ResponseEntity<AppleLoginResponse> signupApple(@RequestBody AppleLoginRequest request) {
        //헤더 dto로 바꾸야함
        AppleLoginResponse response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }
     */


    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtTokens> refreshToken(@RequestHeader(value = "RefreshToken") String refreshToken){
        JwtTokens jwtTokens = authService.reissue(refreshToken);
        return ResponseEntity.ok(jwtTokens);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        return ResponseEntity.ok().build();
    }

    /**
     * 회원탈퇴
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawUser(/*수정*/@RequestBody Long userId){
        boolean isDeleted = authService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
