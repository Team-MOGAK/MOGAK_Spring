package com.mogak.spring.web.controller;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.login.AuthHandler;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.web.dto.JogakRequestDto;
import com.mogak.spring.web.dto.JogakResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "조각 API", description = "조각 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/modarats/mogaks/jogaks")
public class JogakController {
    private final JogakService jogakService;
    private final AuthHandler authHandler;

    @Operation(summary = "조각 생성", description = "조각을 생성합니다",
            parameters = @Parameter(name = "mogakId", description = "모각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 생성"),
                    @ApiResponse(responseCode = "400", description = "진행중인 모각만 조각을 생성",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("")
    public ResponseEntity<BaseResponse<JogakResponseDto.CreateJogakDto>> create(@RequestBody JogakRequestDto.CreateJogakDto createJogakDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(jogakService.createJogak(createJogakDto)));
    }

//    @Operation(summary = "당일 조각 조회", description = "당일 조각을 조회합니다",
//            security = @SecurityRequirement(name = "Bearer Authentication"),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "조회 성공"),
//                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            })
//    @GetMapping("")
//    public ResponseEntity<BaseResponse<GetJogakListDto>> getDailyJogaks() {
//        List<Jogak> jogakList = jogakService.getDailyJogaks(authHandler.getUserId());
//        return ResponseEntity.ok(new BaseResponse<>(JogakConverter.toGetJogakListResponseDto(jogakList)));
//    }

//    @Operation(summary = "조각 시작", description = "조각을 시작합니다",
//            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "조각 시작"),
//                    @ApiResponse(responseCode = "400", description = "자정을 넘어서 조각 시작",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//                    @ApiResponse(responseCode = "409", description = "이미 시작한 조각",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            })
//    @PutMapping("{jogakId}/start")
//    public ResponseEntity<BaseResponse<startJogakDto>> startJogak(@PathVariable Long jogakId) {
//        Jogak jogak = jogakService.startJogak(jogakId);
//        return ResponseEntity.ok(new BaseResponse<>(JogakConverter.toGetStartJogakDto(jogak)));
//    }

//    @Operation(summary = "조각 종료", description = "조각을 종료합니다",
//            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "조각 종료"),
//                    @ApiResponse(responseCode = "400", description = "시작하지 않은 조각, 기한을 넘긴 조각",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//                    @ApiResponse(responseCode = "409", description = "이미 종료한 조각",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            })
//    @PutMapping("{jogakId}/end")
//    public ResponseEntity<BaseResponse<endJogakDto>> endJogak(@PathVariable Long jogakId) {
//        Jogak jogak = jogakService.endJogak(jogakId);
//        return ResponseEntity.ok(new BaseResponse<>(JogakConverter.toEndJogakDto(jogak)));
//    }

    @Operation(summary = "조각 수정", description = "입력값을 이용해 조각을 수정합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/{jogakId}")
    public ResponseEntity<BaseResponse<ErrorCode>> updateJogak(@PathVariable Long jogakId, @RequestBody JogakRequestDto.UpdateJogakDto updateJogakDto) {
        jogakService.updateJogak(jogakId, updateJogakDto);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    @Operation(summary = "조각 삭제", description = "조각을 삭제합니다",
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/{jogakId}")
    public ResponseEntity<BaseResponse<ErrorCode>> deleteJogak(@PathVariable Long jogakId) {
        jogakService.deleteJogak(jogakId);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

}
