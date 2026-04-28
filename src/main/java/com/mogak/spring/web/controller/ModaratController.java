package com.mogak.spring.web.controller;

import com.mogak.spring.jwt.AuthenticatedUser;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.service.ModaratService;
import com.mogak.spring.service.result.modarat.ModaratDetailResult;
import com.mogak.spring.service.result.modarat.ModaratResult;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;
import com.mogak.spring.web.dto.modaratdto.ModaratResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static com.mogak.spring.web.dto.modaratdto.ModaratResponseDto.ModaratDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "모다라트 API", description = "모다라트 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/modarats")
public class ModaratController {
    private final ModaratService modaratService;

    @Operation(summary = "모다라트 생성", description = "입력값을 이용해 모다라트를 생성합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "모각 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "입력 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "잘못된 토큰, 만료된 토큰 등",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("")
    public ResponseEntity<BaseResponse<ModaratDto>> createModarat(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                   @Valid @RequestBody ModaratRequestDto.CreateModaratDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(ModaratDto.from(modaratService.create(authenticatedUser.getUserId(), request))));
    }

    @Operation(summary = "모다라트 삭제", description = "모다라트를 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모다라트 삭제 성공")
            })
    @DeleteMapping("{modaratId}")
    public ResponseEntity<Void> deleteModarat(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                              @PathVariable Long modaratId) {
        modaratService.delete(authenticatedUser.getUserId(), modaratId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모다라트 수정", description = "입력값을 이용해 모다라트를 수정합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모다라트 수정 성공"),
            })
    @PutMapping("/{modaratId}")
    public ResponseEntity<BaseResponse<ModaratDto>> updateModarat(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                                  @PathVariable Long modaratId,
                                                                  @Valid @RequestBody ModaratRequestDto.UpdateModaratDto request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(ModaratDto.from(modaratService.update(authenticatedUser.getUserId(), modaratId, request))));
    }

    @Operation(summary = "단일 모다라트 상세조회", description = "단일 모다라트의 정보를 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "없는 모다라트 조회",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{modaratId}")
    public ResponseEntity<BaseResponse<ModaratResponseDto.DetailModaratDto>> getSingleDetailModarat(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long modaratId
    ) {
        ModaratDetailResult result = modaratService.getDetailModarat(authenticatedUser.getUserId(), modaratId);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(toDetailModaratDto(result)));
    }

    @Operation(summary = "모다라트 리스트 조회", description = "사용자의 모다라트 리스트를 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "잘못된 토큰, 만료된 토큰 등",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("")
    public ResponseEntity<BaseResponse<List<ModaratDto>>> getModaratList(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        List<ModaratResult> results = modaratService.getModaratList(authenticatedUser.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(results.stream().map(this::toModaratDto).toList()));
    }

    private ModaratDto toModaratDto(ModaratResult result) {
        return new ModaratDto(result.id(), result.title(), result.color());
    }

    private ModaratResponseDto.DetailModaratDto toDetailModaratDto(ModaratDetailResult result) {
        return new ModaratResponseDto.DetailModaratDto(
                result.id(),
                result.title(),
                result.color(),
                result.mogaks().stream()
                        .map(this::toMogakInModaratDto)
                        .toList()
        );
    }

    private ModaratResponseDto.MogakInModaratDto toMogakInModaratDto(ModaratDetailResult.MogakInModaratResult result) {
        return new ModaratResponseDto.MogakInModaratDto(
                result.title(),
                result.bigCategory(),
                result.smallCategory(),
                result.color()
        );
    }
}
