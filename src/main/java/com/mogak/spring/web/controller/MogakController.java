package com.mogak.spring.web.controller;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.web.dto.MogakRequestDto;
import com.mogak.spring.web.dto.MogakResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mogak")
public class MogakController {
    private final MogakService mogakService;

    @PostMapping("/")
    public ResponseEntity<MogakResponseDto.createDto> createMogak(MogakRequestDto.CreateDto request) {
        Mogak mogak = mogakService.create(request);
        return ResponseEntity.ok(MogakConverter.toCreateDto);
    }
}
