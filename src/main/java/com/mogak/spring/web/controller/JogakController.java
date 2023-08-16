package com.mogak.spring.web.controller;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.service.JogakService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "조각 API", description = "조각 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mogaks/jogaks")
public class JogakController {
    private final JogakService jogakService;

    /**
     * 임시 조각 생성 API
     * */
    @Operation(summary = "(임시)조각 생성", description = "모각에 대한 조각을 생성합니다",
            parameters = @Parameter(name = "mogakId", description = "모각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 생성"),
                    @ApiResponse(responseCode = "400", description = "진행중인 모각만 조각을 생성",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/{mogakId}")
    public ResponseEntity<Object> create(@PathVariable Long mogakId) {
        Jogak jogak = jogakService.createJogak(mogakId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JogakConverter.toCreateJogakResponseDto(jogak));
    }

    /**
     * 당일 조각 조회 API
     * */
    @Operation(summary = "당일 조각 조회", description = "당일 조각을 조회합니다",
            parameters = @Parameter(name = "userId", description = "유저 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("")
    public ResponseEntity<JogakResponseDto.GetJogakListDto> getDailyJogaks(HttpServletRequest req) {
        List<Jogak> jogakList = jogakService.getDailyJogaks(req);
        return ResponseEntity.status(HttpStatus.OK)
                .body(JogakConverter.toGetJogakListResponseDto(jogakList));
    }

    /**
     *  조각 시작 API
     * */
    @Operation(summary = "조각 시작", description = "조각을 시작합니다",
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 시작"),
                    @ApiResponse(responseCode = "400", description = "자정을 넘어서 조각 시작",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 시작한 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/{jogakId}/start")
    public ResponseEntity<JogakResponseDto.startJogakDto> startJogak(@PathVariable Long jogakId) {
        Jogak jogak = jogakService.startJogak(jogakId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(JogakConverter.toGetStartJogakDto(jogak));
    }

    /**
     *  조각 종료 API
     * */
    @Operation(summary = "조각 종료", description = "조각을 종료합니다",
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 종료"),
                    @ApiResponse(responseCode = "400", description = "시작하지 않은 조각, 기한을 넘긴 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 종료한 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/{jogakId}/end")
    public ResponseEntity<JogakResponseDto.endJogakDto> endJogak(@PathVariable Long jogakId) {
        Jogak jogak = jogakService.endJogak(jogakId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(JogakConverter.toEndJogakDto(jogak));
    }

    /**
     * 임시 조각 삭제 API
     * */
    @Operation(summary = "(임시)조각 삭제", description = "조각을 삭제합니다",
            parameters = @Parameter(name = "jogakId", description = "조각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조각 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 조각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/{jogakId}")
    public ResponseEntity<Void> deleteJogak(@PathVariable Long jogakId) {
        jogakService.deleteJogak(jogakId);
        return ResponseEntity.noContent().build();
    }

}
