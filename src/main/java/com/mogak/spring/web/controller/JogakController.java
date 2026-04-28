package com.mogak.spring.web.controller;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.result.jogak.CreateJogakResult;
import com.mogak.spring.service.result.jogak.DailyJogakListResult;
import com.mogak.spring.service.result.jogak.DailyJogakResult;
import com.mogak.spring.service.result.jogak.DetailJogakResult;
import com.mogak.spring.service.result.jogak.JogakDailyJogakResult;
import com.mogak.spring.service.result.jogak.OneTimeJogakListResult;
import com.mogak.spring.service.result.jogak.OneTimeJogakResult;
import com.mogak.spring.service.result.jogak.RoutineJogakResult;
import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
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

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "조각 API", description = "조각 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/modarats/mogaks/jogaks")
public class JogakController {
    private final JogakService jogakService;

    @Operation(summary = "조각 생성", description = "조각을 생성합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 생성"),
                    @ApiResponse(responseCode = "400", description = "진행중인 모각만 조각을 생성",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("")
    public ResponseEntity<BaseResponse<JogakResponseDto.CreateJogakDto>> create(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                 @Valid @RequestBody JogakRequestDto.CreateJogakDto createJogakDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(
                toCreateJogakDto(jogakService.createJogak(authenticatedUser.getUserId(), createJogakDto))
        ));
    }

    @Operation(summary = "단일 조각 조회", description = "조각 ID를 통해 조각 정보를 조회하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/{jogakId}/detail")
    public ResponseEntity<BaseResponse<JogakResponseDto.DetailJogakDto>> getJogakDetail(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                         @PathVariable Long jogakId) {
        return ResponseEntity.ok(new BaseResponse<>(
                toDetailJogakDto(jogakService.getJogakDetail(authenticatedUser.getUserId(), jogakId))
        ));
    }


    @Operation(summary = "일회성 조각 조회", description = "일회성 조각들을 조회하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/daily")
    public ResponseEntity<BaseResponse<JogakResponseDto.GetOneTimeJogakListDto>> getDailyJogaks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "조회를 원하는 날짜를 입력해주시면 됩니다. format: YYYY-MM-DD", example = "2024-02-15")
            @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(new BaseResponse<>(
                toGetOneTimeJogakListDto(jogakService.getDailyJogaks(authenticatedUser.getUserId(), date))
        ));
    }

    @Operation(summary = "일별 데일리 조각 조회", description = "일별 데일리 조각들을 조회하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping
    public ResponseEntity<BaseResponse<JogakResponseDto.GetDailyJogakListDto>> getDayJogaks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "조회를 원하는 날짜를 입력해주시면 됩니다. format: YYYY-MM-DD", example = "2024-02-14")
            @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(new BaseResponse<>(
                toGetDailyJogakListDto(jogakService.getDayJogaks(authenticatedUser.getUserId(), date))
        ));
    }

    @Operation(summary = "주간/월간 루틴 조각 조회", description = "주간/월간 루틴 조각을 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/routines")
    public ResponseEntity<BaseResponse<List<JogakResponseDto.GetRoutineJogakDto>>> getRoutineJogaks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "조회를 원하는 첫 날짜를 입력. format: YYYY-MM-DD", example = "2024-02-14")
            @RequestParam("startDay") @DateTimeFormat(iso = ISO.DATE) LocalDate startDay,
            @Parameter(description = "조회를 원하는 마지막 날짜를 입력. format: YYYY-MM-DD", example = "2024-02-15")
            @RequestParam("endDay") @DateTimeFormat(iso = ISO.DATE) LocalDate endDay) {
        return ResponseEntity.ok(new BaseResponse<>(
                toGetRoutineJogakDtos(jogakService.getRoutineJogaks(authenticatedUser.getUserId(), startDay, endDay))
        ));
    }

    @Operation(summary = "일일 조각 시작", description = "일일 조각을 시작합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 시작"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 시작한 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("{jogakId}/start")
    public ResponseEntity<BaseResponse<JogakResponseDto.JogakDailyJogakDto>> startJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                        @PathVariable Long jogakId) {
        return ResponseEntity.ok(new BaseResponse<>(
                toJogakDailyJogakDto(jogakService.startJogak(authenticatedUser.getUserId(), jogakId))
        ));
    }

    @Operation(summary = "조각 성공", description = "오늘의 조각으로 등록된 조각을 성공시킵니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "dailyJogakId", description = "데일리 조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 종료"),
                    @ApiResponse(responseCode = "400", description = "시작하지 않은 조각, 기한을 넘긴 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 종료한 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("{dailyJogakId}/success")
    public ResponseEntity<BaseResponse<JogakResponseDto.JogakDailyJogakDto>> successJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                           @PathVariable Long dailyJogakId) {
        return ResponseEntity.ok(new BaseResponse<>(
                toJogakDailyJogakDto(jogakService.successJogak(authenticatedUser.getUserId(), dailyJogakId))
        ));
    }

    @Operation(summary = "조각 실패", description = "성공한 조각을 취소합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "dailyJogakId", description = "데일리 조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 종료"),
                    @ApiResponse(responseCode = "400", description = "시작하지 않은 조각, 기한을 넘긴 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 종료한 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("{dailyJogakId}/fail")
    public ResponseEntity<BaseResponse<JogakResponseDto.JogakDailyJogakDto>> failJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                        @PathVariable Long dailyJogakId) {
        return ResponseEntity.ok(new BaseResponse<>(
                toJogakDailyJogakDto(jogakService.failJogak(authenticatedUser.getUserId(), dailyJogakId))
        ));
    }

    @Operation(summary = "조각 수정", description = "입력값을 이용해 조각을 수정합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/{jogakId}")
    public ResponseEntity<BaseResponse<JogakResponseDto.CreateJogakDto>> updateJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                               @PathVariable Long jogakId,
                                                               @Valid @RequestBody JogakRequestDto.UpdateJogakDto updateJogakDto) {
        return ResponseEntity.ok(new BaseResponse<>(
                toCreateJogakDto(jogakService.updateJogak(authenticatedUser.getUserId(), jogakId, updateJogakDto))
        ));
    }

    @Operation(summary = "조각 삭제", description = "조각을 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/{jogakId}")
    public ResponseEntity<BaseResponse<ErrorCode>> deleteJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                               @PathVariable Long jogakId) {
        jogakService.deleteJogak(authenticatedUser.getUserId(), jogakId);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    private JogakResponseDto.CreateJogakDto toCreateJogakDto(CreateJogakResult result) {
        return JogakResponseDto.CreateJogakDto.of(
                result.jogakId(),
                result.mogakTitle(),
                result.category(),
                result.title(),
                result.isRoutine(),
                result.days(),
                result.achievements(),
                result.startDate(),
                result.endDate()
        );
    }

    private JogakResponseDto.DetailJogakDto toDetailJogakDto(DetailJogakResult result) {
        return JogakResponseDto.DetailJogakDto.of(
                result.jogakId(),
                result.mogakTitle(),
                result.category(),
                result.title(),
                result.isRoutine(),
                result.days(),
                result.color(),
                result.achievements(),
                result.startDate(),
                result.endDate()
        );
    }

    private JogakResponseDto.GetDailyJogakListDto toGetDailyJogakListDto(DailyJogakListResult result) {
        List<JogakResponseDto.GetDailyJogakDto> dailyJogaks = result.dailyJogaks().stream()
                .map(this::toGetDailyJogakDto)
                .toList();
        return new JogakResponseDto.GetDailyJogakListDto(result.size(), dailyJogaks);
    }

    private JogakResponseDto.GetDailyJogakDto toGetDailyJogakDto(DailyJogakResult result) {
        return JogakResponseDto.GetDailyJogakDto.of(
                result.jogakId(),
                result.dailyJogakId(),
                result.mogakTitle(),
                result.category(),
                result.title(),
                result.isRoutine(),
                result.isAchievement()
        );
    }

    private JogakResponseDto.GetOneTimeJogakListDto toGetOneTimeJogakListDto(OneTimeJogakListResult result) {
        List<JogakResponseDto.GetOneTimeJogakDto> jogaks = result.jogaks().stream()
                .map(this::toGetOneTimeJogakDto)
                .toList();
        return new JogakResponseDto.GetOneTimeJogakListDto(result.size(), jogaks);
    }

    private JogakResponseDto.GetOneTimeJogakDto toGetOneTimeJogakDto(OneTimeJogakResult result) {
        return JogakResponseDto.GetOneTimeJogakDto.of(
                result.jogakId(),
                result.mogakTitle(),
                result.category(),
                result.title(),
                result.isRoutine(),
                result.isAlreadyAdded(),
                result.achievements(),
                result.startDate(),
                result.endDate()
        );
    }

    private List<JogakResponseDto.GetRoutineJogakDto> toGetRoutineJogakDtos(List<RoutineJogakResult> results) {
        return results.stream()
                .map(this::toGetRoutineJogakDto)
                .toList();
    }

    private JogakResponseDto.GetRoutineJogakDto toGetRoutineJogakDto(RoutineJogakResult result) {
        return JogakResponseDto.GetRoutineJogakDto.of(
                result.dailyJogakId(),
                result.date(),
                result.isAchievement(),
                result.title()
        );
    }

    private JogakResponseDto.JogakDailyJogakDto toJogakDailyJogakDto(JogakDailyJogakResult result) {
        return JogakResponseDto.JogakDailyJogakDto.of(
                result.jogakId(),
                result.dailyJogakId(),
                result.title(),
                result.mogakTitle(),
                result.category(),
                result.isRoutine(),
                result.days(),
                result.isAchievement(),
                result.achievements()
        );
    }

}
