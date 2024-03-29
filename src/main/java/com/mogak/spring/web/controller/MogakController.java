package com.mogak.spring.web.controller;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@Tag(name = "모각 API", description = "모각 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/modarats")
public class MogakController {
    private final MogakService mogakService;

    @Operation(summary = "모각 생성", description = "입력값을 이용해 모각을 생성합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "모각 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X, 시작,끝 날짜 역전, 잘못 입력된 시작 날짜",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "토큰 문제",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/mogaks")
    public ResponseEntity<BaseResponse<MogakResponseDto.GetMogakDto>> createMogak(@Valid @RequestBody MogakRequestDto.CreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(mogakService.create(request)));
    }

//    @Operation(summary = "모각 달성", description = "해당하는 모각을 달성합니다",
//            security = @SecurityRequirement(name = "Bearer Authentication"),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "모각 달성"),
//                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
//                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            })
//    @PutMapping("/mogaks/{mogakId}/complete")
//    public ResponseEntity<BaseResponse<MogakResponseDto.UpdateStateDto>> achieveMogak(@PathVariable Long mogakId) {
//        return ResponseEntity.ok(new BaseResponse<>(mogakService.achieveMogak(mogakId)));
//    }

    @Operation(summary = "모각 수정", description = "입력값을 이용해 모각을 수정합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping("/mogaks")
    public ResponseEntity<BaseResponse<MogakResponseDto.GetMogakDto>> updateMogak(@Valid @RequestBody MogakRequestDto.UpdateDto request) {
        return ResponseEntity.ok(new BaseResponse<>(mogakService.updateMogak(request)));
    }

    @Operation(summary = "모각 조회", description = "입력값을 이용해 모각을 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/{modaratId}/mogaks")
    public ResponseEntity<BaseResponse<MogakResponseDto.GetMogakListDto>> getMogakList(@PathVariable Long modaratId) {
            return ResponseEntity.ok(new BaseResponse<>(mogakService.getMogakDtoList(modaratId)));
    }

    @Operation(summary = "모각 삭제", description = "모각을 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "mogakId", description = "모각 ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/mogaks/{mogakId}")
    public ResponseEntity<BaseResponse<ErrorCode>> deleteMogak(@PathVariable Long mogakId) {
        mogakService.deleteMogak(mogakId);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    @Operation(summary = "조각 조회", description = "모각의 조각을 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "mogakId", description = "모각 ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/mogaks/{mogakId}/jogaks")
    public ResponseEntity<BaseResponse<List<JogakResponseDto.GetJogakDto>>> getJogaks(
            @PathVariable Long mogakId,
            @Parameter(description = "조회를 원하는 날짜를 입력해주시면 됩니다. 오늘 날짜를 주로 입력하시면 됩니다. format: YYYY-MM-DD", example = "2024-02-15")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(new BaseResponse<>(mogakService.getJogaks(mogakId, date)));
    }
}
