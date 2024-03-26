package com.mogak.spring.web.controller;

import com.mogak.spring.converter.ModaratConverter;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.login.AuthHandler;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import com.mogak.spring.service.ModaratService;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;
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

import javax.validation.Valid;
import java.util.List;

import static com.mogak.spring.web.dto.modaratdto.ModaratResponseDto.ModaratDto;

@Tag(name = "모다라트 API", description = "모다라트 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/modarats")
public class ModaratController {
    private final ModaratService modaratService;
    private final AuthHandler authHandler;

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
    public ResponseEntity<BaseResponse<ModaratDto>> createModarat(@Valid @RequestBody ModaratRequestDto.CreateModaratDto request) {
        Modarat modarat = modaratService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(ModaratConverter.toModaratDto(modarat)));
    }

    @Operation(summary = "모다라트 삭제", description = "모다라트를 삭제합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "모각 삭제 성공")
            })
    @DeleteMapping("{modaratId}")
    public ResponseEntity<BaseResponse<Void>> deleteModarat(@PathVariable Long modaratId) {
        modaratService.delete(modaratId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "모다라트 수정", description = "입력값을 이용해 모다라트를 수정합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "모각 수정 성공"),
            })
    @PutMapping("/{modaratId}")
    public ResponseEntity<BaseResponse<ModaratDto>> updateModarat(@PathVariable Long modaratId,
                                                                  @Valid @RequestBody ModaratRequestDto.UpdateModaratDto request) {
        Modarat modarat = modaratService.update(modaratId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(ModaratConverter.toModaratDto(modarat)));
    }

    @Operation(summary = "단일 모다라트 상세조회", description = "단일 모다라트의 정보를 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "없는 모다라트 조회",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{modaratId}")
    public ResponseEntity<BaseResponse<SingleDetailModaratDto>> getSingleDetailModarat(@PathVariable Long modaratId) {
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(modaratService.getDetailModarat(modaratId)));
    }

    @Operation(summary = "모다라트 리스트 조회", description = "사용자의 모다라트 리스트를 조회합니다",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "잘못된 토큰, 만료된 토큰 등",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("")
    public ResponseEntity<BaseResponse<List<ModaratDto>>> getModaratList() {
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(modaratService.getModaratList()));
    }

}
