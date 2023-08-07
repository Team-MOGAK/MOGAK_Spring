package com.mogak.spring.web.controller;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.web.dto.JogakRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mogaks/jogaks")
public class JogakController {
    private final JogakService jogakService;

    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody JogakRequestDto.CreateJogakDto request) {
        Jogak jogak = jogakService.createJogak(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JogakConverter.toJogakResponseDto(jogak));
    }


}
