package com.mogak.spring.web.controller;

import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.service.MetadataService;
import com.mogak.spring.service.result.metadata.MetadataOptionResult;
import com.mogak.spring.web.dto.metadatadto.MetadataOptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "메타데이터 API", description = "메타데이터 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/metadata")
public class MetadataController {
    private final MetadataService metadataService;

    @Operation(summary = "직업 메타데이터 조회", description = "회원가입 및 프로필에서 사용하는 직업 목록을 조회합니다")
    @GetMapping("/jobs")
    public ResponseEntity<BaseResponse<List<MetadataOptionResponse>>> getJobs() {
        return ResponseEntity.ok(new BaseResponse<>(toMetadataOptionResponses(metadataService.getJobs())));
    }

    @Operation(summary = "주소 메타데이터 조회", description = "회원가입 및 프로필에서 사용하는 주소 목록을 조회합니다")
    @GetMapping("/addresses")
    public ResponseEntity<BaseResponse<List<MetadataOptionResponse>>> getAddresses() {
        return ResponseEntity.ok(new BaseResponse<>(toMetadataOptionResponses(metadataService.getAddresses())));
    }

    @Operation(summary = "모각 카테고리 메타데이터 조회", description = "모각 생성에 사용하는 카테고리 목록을 조회합니다")
    @GetMapping("/mogak-categories")
    public ResponseEntity<BaseResponse<List<MetadataOptionResponse>>> getMogakCategories() {
        return ResponseEntity.ok(new BaseResponse<>(toMetadataOptionResponses(metadataService.getMogakCategories())));
    }

    @Operation(summary = "색상 메타데이터 조회", description = "모다라트 및 모각 생성에 사용하는 색상 목록을 조회합니다")
    @GetMapping("/colors")
    public ResponseEntity<BaseResponse<List<MetadataOptionResponse>>> getColors() {
        return ResponseEntity.ok(new BaseResponse<>(toMetadataOptionResponses(metadataService.getColors())));
    }

    private List<MetadataOptionResponse> toMetadataOptionResponses(List<MetadataOptionResult> results) {
        return results.stream()
                .map(MetadataOptionResponse::from)
                .toList();
    }
}
