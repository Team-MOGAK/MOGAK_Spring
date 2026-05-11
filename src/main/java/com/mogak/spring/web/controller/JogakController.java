package com.mogak.spring.web.controller;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.service.command.CreateJogakCommand;
import com.mogak.spring.service.command.UpdateJogakCommand;
import com.mogak.spring.service.result.CreateJogakResult;
import com.mogak.spring.service.result.DailyJogakListResult;
import com.mogak.spring.service.result.DailyJogakResult;
import com.mogak.spring.service.result.JogakDailyResult;
import com.mogak.spring.service.result.JogakDetailResult;
import com.mogak.spring.service.result.OneTimeJogakListResult;
import com.mogak.spring.service.result.OneTimeJogakResult;
import com.mogak.spring.service.result.RoutineJogakResult;
import com.mogak.spring.web.dto.jogakdto.CreateJogakResponse;
import com.mogak.spring.web.dto.jogakdto.CreateJogakRequest;
import com.mogak.spring.web.dto.jogakdto.DailyJogakListResponse;
import com.mogak.spring.web.dto.jogakdto.DailyJogakResponse;
import com.mogak.spring.web.dto.jogakdto.JogakDailyResponse;
import com.mogak.spring.web.dto.jogakdto.JogakDetailResponse;
import com.mogak.spring.web.dto.jogakdto.OneTimeJogakListResponse;
import com.mogak.spring.web.dto.jogakdto.OneTimeJogakResponse;
import com.mogak.spring.web.dto.jogakdto.RoutineJogakResponse;
import com.mogak.spring.web.dto.jogakdto.UpdateJogakRequest;

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
@RequestMapping("/api")
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
    @PostMapping("/jogaks")
    public ResponseEntity<BaseResponse<CreateJogakResponse>> create(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                 @Valid @RequestBody CreateJogakRequest createJogakRequest) {
        CreateJogakResult result = jogakService.createJogak(
                authenticatedUser.getUserId(),
                new CreateJogakCommand(
                        createJogakRequest.mogakId(),
                        createJogakRequest.title(),
                        createJogakRequest.isRoutine(),
                        createJogakRequest.days(),
                        createJogakRequest.today(),
                        createJogakRequest.endDate()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(toCreateJogakResponse(result)));
    }

    @Operation(summary = "단일 조각 조회", description = "조각 ID를 통해 조각 정보를 조회하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/jogaks/{jogakId}")
    public ResponseEntity<BaseResponse<JogakDetailResponse>> getJogakDetail(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                         @PathVariable Long jogakId) {
        return ResponseEntity.ok(new BaseResponse<>(toJogakDetailResponse(jogakService.getJogakDetail(authenticatedUser.getUserId(), jogakId))));
    }


    @Operation(summary = "일회성 조각 조회", description = "일회성 조각들을 조회하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/jogaks/daily")
    public ResponseEntity<BaseResponse<OneTimeJogakListResponse>> getDailyJogaks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "조회를 원하는 날짜를 입력해주시면 됩니다. format: YYYY-MM-DD", example = "2024-02-15")
            @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(new BaseResponse<>(toOneTimeJogakListResponse(jogakService.getDailyJogaks(authenticatedUser.getUserId(), date))));
    }

    @Operation(summary = "일별 데일리 조각 조회", description = "일별 데일리 조각들을 조회하는 API",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/jogaks")
    public ResponseEntity<BaseResponse<DailyJogakListResponse>> getDayJogaks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "조회를 원하는 날짜를 입력해주시면 됩니다. format: YYYY-MM-DD", example = "2024-02-14")
            @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(new BaseResponse<>(toDailyJogakListResponse(jogakService.getDayJogaks(authenticatedUser.getUserId(), date))));
    }

    @Operation(summary = "주간/월간 루틴 조각 조회", description = "주간/월간 루틴 조각을 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/jogaks/routines")
    public ResponseEntity<BaseResponse<List<RoutineJogakResponse>>> getRoutineJogaks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Parameter(description = "조회를 원하는 첫 날짜를 입력. format: YYYY-MM-DD", example = "2024-02-14")
            @RequestParam("startDay") @DateTimeFormat(iso = ISO.DATE) LocalDate startDay,
            @Parameter(description = "조회를 원하는 마지막 날짜를 입력. format: YYYY-MM-DD", example = "2024-02-15")
            @RequestParam("endDay") @DateTimeFormat(iso = ISO.DATE) LocalDate endDay) {
        return ResponseEntity.ok(new BaseResponse<>(jogakService.getRoutineJogaks(authenticatedUser.getUserId(), startDay, endDay).stream()
                .map(this::toRoutineJogakResponse)
                .toList()));
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
    @PostMapping("/jogaks/{jogakId}/start")
    public ResponseEntity<BaseResponse<JogakDailyResponse>> startJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                        @PathVariable Long jogakId) {
        return ResponseEntity.ok(new BaseResponse<>(toJogakDailyResponse(jogakService.startJogak(authenticatedUser.getUserId(), jogakId))));
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
    @PutMapping("/daily-jogaks/{dailyJogakId}/success")
    public ResponseEntity<BaseResponse<JogakDailyResponse>> successJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                           @PathVariable Long dailyJogakId) {
        return ResponseEntity.ok(new BaseResponse<>(toJogakDailyResponse(jogakService.successJogak(authenticatedUser.getUserId(), dailyJogakId))));
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
    @PutMapping("/daily-jogaks/{dailyJogakId}/fail")
    public ResponseEntity<BaseResponse<JogakDailyResponse>> failJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                                        @PathVariable Long dailyJogakId) {
        return ResponseEntity.ok(new BaseResponse<>(toJogakDailyResponse(jogakService.failJogak(authenticatedUser.getUserId(), dailyJogakId))));
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
    @PutMapping("/jogaks/{jogakId}")
    public ResponseEntity<BaseResponse<CreateJogakResponse>> updateJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                               @PathVariable Long jogakId,
                                                               @Valid @RequestBody UpdateJogakRequest updateJogakRequest) {
        CreateJogakResult result = jogakService.updateJogak(
                authenticatedUser.getUserId(),
                jogakId,
                new UpdateJogakCommand(
                        updateJogakRequest.title(),
                        updateJogakRequest.isRoutine(),
                        updateJogakRequest.days(),
                        updateJogakRequest.endDate()
                )
        );
        return ResponseEntity.ok(new BaseResponse<>(toCreateJogakResponse(result)));
    }

    @Operation(summary = "조각 삭제", description = "조각을 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/jogaks/{jogakId}")
    public ResponseEntity<BaseResponse<ErrorCode>> deleteJogak(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                               @PathVariable Long jogakId) {
        jogakService.deleteJogak(authenticatedUser.getUserId(), jogakId);
        return ResponseEntity.ok(new BaseResponse<>(ErrorCode.SUCCESS));
    }

    private CreateJogakResponse toCreateJogakResponse(CreateJogakResult result) {
        return new CreateJogakResponse(
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

    private JogakDetailResponse toJogakDetailResponse(JogakDetailResult result) {
        return new JogakDetailResponse(
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

    private OneTimeJogakListResponse toOneTimeJogakListResponse(OneTimeJogakListResult result) {
        return new OneTimeJogakListResponse(
                result.size(),
                result.jogaks().stream().map(this::toOneTimeJogakResponse).toList()
        );
    }

    private OneTimeJogakResponse toOneTimeJogakResponse(OneTimeJogakResult result) {
        return new OneTimeJogakResponse(
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

    private DailyJogakListResponse toDailyJogakListResponse(DailyJogakListResult result) {
        return new DailyJogakListResponse(
                result.size(),
                result.dailyJogaks().stream().map(this::toDailyJogakResponse).toList()
        );
    }

    private DailyJogakResponse toDailyJogakResponse(DailyJogakResult result) {
        return new DailyJogakResponse(
                result.jogakId(),
                result.dailyJogakId(),
                result.mogakTitle(),
                result.category(),
                result.title(),
                result.isRoutine(),
                result.isAchievement()
        );
    }

    private RoutineJogakResponse toRoutineJogakResponse(RoutineJogakResult result) {
        return new RoutineJogakResponse(result.dailyJogakId(), result.date(), result.isAchievement(), result.title());
    }

    private JogakDailyResponse toJogakDailyResponse(JogakDailyResult result) {
        return new JogakDailyResponse(
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
