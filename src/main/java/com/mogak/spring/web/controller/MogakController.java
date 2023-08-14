package com.mogak.spring.web.controller;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.web.dto.MogakRequestDto;
import com.mogak.spring.web.dto.MogakResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "모각 API", description = "모각 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mogaks")
public class MogakController {
    private final MogakService mogakService;

    /**
     * 모각 생성 API
     * */
    @Operation(summary = "모각 생성", description = "입력값을 이용해 모각을 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "201", description = "모각 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X, 시작,끝 날짜 역전, 잘못 입력된 시작 날짜",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자, 존재하지 않는 카테고리, 존재하지 않는 요일",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("")
    public ResponseEntity<MogakResponseDto.CreateDto> createMogak(@RequestBody MogakRequestDto.CreateDto request) {
        Mogak mogak = mogakService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MogakConverter.toCreateDto(mogak));
    }

    /**
     * 모각 달성하기 API
     * */
    @Operation(summary = "모각 달성", description = "해당하는 모각을 달성합니다",
            parameters = @Parameter(name = "id", description = "모각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 달성"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/{id}/complete")
    public ResponseEntity<MogakResponseDto.UpdateStateDto> achieveMogak(@PathVariable Long id) {
        Mogak mogak = mogakService.achieveMogak(id);
        return ResponseEntity.status(HttpStatus.OK).body(MogakConverter.toUpdateDto(mogak));
    }

    /**
     * 모각 수정 API
     * */
    @Operation(summary = "모각 수정", description = "입력값을 이용해 모각을 수정합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "기타 카테고리 X",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각, 존재하지 않는 카테고리",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("")
    public ResponseEntity<MogakResponseDto.UpdateStateDto> updateMogak(@RequestBody MogakRequestDto.UpdateDto request) {
        Mogak mogak = mogakService.updateMogak(request);
        return ResponseEntity.status(HttpStatus.OK).body(MogakConverter.toUpdateDto(mogak));
    }

    /**
     * 모각 조회 API
     * user => 유저 PK
     * cursor => 데이터 조회 시작점
     * size => 조회할 데이터 개수
     * */
    @Operation(summary = "모각 조회", description = "입력값을 이용해 모각을 페이징 조회합니다",
            parameters = {
                    @Parameter(name = "userId", description = "유저 PK"),
                    @Parameter(name = "cursor", description = "페이징 커서"),
                    @Parameter(name = "size", description = "페이징 개수")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("")
    public ResponseEntity<MogakResponseDto.GetMogakListDto> getMogakList(
            @RequestParam(value = "user") Long userId,
            @RequestParam(value = "cursor") int cursor,
            @RequestParam(value = "size") int size) {
            List<Mogak> mogaks = mogakService.getMogakList(userId, cursor, size);
            return ResponseEntity.status(HttpStatus.OK).body(MogakConverter.toGetMogakListDto(mogaks));
    }

    /**
     * 모각 삭제 API
     * */
    @Operation(summary = "모각 삭제", description = "모각을 삭제합니다",
            parameters = @Parameter(name = "mogakId", description = "모각 ID"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 모각",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @DeleteMapping("/{mogakId}")
    public ResponseEntity<Void> deleteMogak(
            @PathVariable Long mogakId) {
        mogakService.deleteMogak(mogakId);
        return ResponseEntity.noContent().build();
    }

}
