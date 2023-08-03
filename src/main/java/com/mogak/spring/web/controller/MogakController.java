package com.mogak.spring.web.controller;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.web.dto.MogakRequestDto;
import com.mogak.spring.web.dto.MogakResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mogaks")
public class MogakController {
    private final MogakService mogakService;

    /**
     * 모각 생성 API
     * */
    @PostMapping("")
    public ResponseEntity<MogakResponseDto.createDto> createMogak(@RequestBody MogakRequestDto.CreateDto request) {
        Mogak mogak = mogakService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MogakConverter.toCreateDto(mogak));
    }

    /**
     * 모각 달성하기 API
     * */
    @PutMapping("/{id}/complete")
    public ResponseEntity<MogakResponseDto.updateStateDto> achieveMogak(@PathVariable Long id) {
        Mogak mogak = mogakService.achieveMogak(id);
        return ResponseEntity.status(HttpStatus.OK).body(MogakConverter.toUpdateDto(mogak));
    }
}
