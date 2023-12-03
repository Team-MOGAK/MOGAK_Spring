package com.mogak.spring.web.controller;

import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.service.AuthService;
import com.mogak.spring.web.dto.AppleLoginRequest;
import com.mogak.spring.web.dto.AppleLoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/*
로그인 테스트를 위함. 이부분 테스트 끝나면 user controller로 옮길 예정
 */
@Tag(name = "로그인 API", description = "로그인 API 명세서")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apple")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
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
