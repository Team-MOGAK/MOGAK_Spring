package com.mogak.spring.web.controller;

import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.domain.user.SocialProvider;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.jwt.JwtTokens;
import com.mogak.spring.service.AuthService;
import com.mogak.spring.web.dto.authdto.AppleLoginRequest;
import com.mogak.spring.web.dto.authdto.AppleLoginResponse;
import com.mogak.spring.web.dto.authdto.AuthResponse;
import com.mogak.spring.web.dto.authdto.SocialLoginRequest;
import com.mogak.spring.web.dto.authdto.SocialLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<BaseResponse<AppleLoginResponse>> loginApple(@RequestBody AppleLoginRequest request) {
        AppleLoginResponse response = authService.appleLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }

    @Operation(
            summary = "소셜 로그인",
            description = """
                    공급자별 소셜 로그인을 합니다.
                    - apple/google: 클라이언트가 공급자에서 받은 id token을 token에 전달합니다.
                    - kakao: 클라이언트가 카카오에서 받은 access token을 token에 전달합니다.
                    - apple/google 신규 회원은 검증된 이메일이 필요합니다.
                    - kakao 신규 회원은 이메일 없이 provider 계정만으로 가입할 수 있습니다.
                    - 기존 이메일 계정이 있으면 자동 연결하지 않고 별도 계정 연결 절차가 필요합니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "400", description = "지원하지 않는 공급자, 잘못된 소셜 토큰, 이메일 누락/미검증"),
                    @ApiResponse(responseCode = "409", description = "기존 이메일 계정 연결 필요 또는 이미 연결된 소셜 계정")
            })
    @PostMapping("/{provider}/login")
    public ResponseEntity<BaseResponse<SocialLoginResponse>> loginSocial(
            @Parameter(
                    description = "소셜 로그인 공급자",
                    example = "google",
                    schema = @Schema(allowableValues = {"apple", "google", "kakao"})
            )
            @PathVariable String provider,
            @RequestBody SocialLoginRequest request
    ) {
        SocialLoginResponse response = authService.socialLogin(SocialProvider.from(provider), request);
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
    public ResponseEntity<BaseResponse<ErrorCode>> logout(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        authService.logout(authenticatedUser.getUserId());
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    /**
     * 회원탈퇴
     */
    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 합니다",
            responses = {@ApiResponse(responseCode = "200", description = "회원퇄퇴 성공"),})
    @PostMapping("/withdraw")
    public ResponseEntity<BaseResponse<AuthResponse.WithdrawDto>> withdrawUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        AuthResponse.WithdrawDto withdrawDto = authService.deleteUser(authenticatedUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(withdrawDto));
    }
}
