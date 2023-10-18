package com.mogak.spring.web.controller;

import com.mogak.spring.converter.ModaratConverter;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.global.BaseResponse;
import com.mogak.spring.login.AuthHandler;
import com.mogak.spring.service.ModaratService;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.mogak.spring.web.dto.ModaratDto.ModaratResponseDto.*;

@Tag(name = "모다라트 API", description = "모다라트 API 명세서")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/modarats")
public class ModaratController {
    private final ModaratService modaratService;
    private final AuthHandler authHandler;

    @PostMapping("")
    public ResponseEntity<BaseResponse<CreateModaratDto>> createModarat(@RequestBody @Valid ModaratRequestDto.CreateModaratDto request) {
        Modarat modarat = modaratService.create(authHandler.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(ModaratConverter.toCreateDto(modarat)));
    }

    @DeleteMapping("{modaratId}")
    public ResponseEntity<BaseResponse<Void>> deleteModarat(@PathVariable Long modaratId) {
        modaratService.delete(modaratId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{modaratId}")
    public ResponseEntity<BaseResponse<CreateModaratDto>> updateModarat(@PathVariable Long modaratId, @RequestBody ModaratRequestDto.UpdateModaratDto request) {
        Modarat modarat = modaratService.update(modaratId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(ModaratConverter.toCreateDto(modarat)));
    }

    @GetMapping("/{modaratId}")
    public ResponseEntity<BaseResponse<SingleDetailModaratDto>> getSingleDetailModarat(@PathVariable Long modaratId) {
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(modaratService.getDetailModarat(modaratId)));
    }

}
