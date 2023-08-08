package com.mogak.spring.web.controller;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.service.JogakService;
import com.mogak.spring.web.dto.JogakResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mogaks/jogaks")
public class JogakController {
    private final JogakService jogakService;

    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody Long request) {
        Jogak jogak = jogakService.createJogak(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JogakConverter.toCreateJogakResponseDto(jogak));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<JogakResponseDto.GetJogakListDto> getDailyJogaks(@PathVariable Long userId) {
        List<Jogak> jogakList = jogakService.getDailyJogaks(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(JogakConverter.toGetJogakListResponseDto(jogakList));
    }

    @PutMapping("/{jogakId}/start")
    public ResponseEntity<JogakResponseDto.startJogakDto> startJogak(@PathVariable Long jogakId) {
        Jogak jogak = jogakService.startJogak(jogakId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(JogakConverter.toGetStartJogakDto(jogak));
    }

    @PutMapping("/{jogakId}/end")
    public ResponseEntity<JogakResponseDto.endJogakDto> endJogak(@PathVariable Long jogakId) {
        Jogak jogak = jogakService.endJogak(jogakId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(JogakConverter.toEndJogakDto(jogak));
    }

    @DeleteMapping("/{jogakId}")
    public ResponseEntity<Void> deleteJogak(@PathVariable Long jogakId) {
        jogakService.deleteJogak(jogakId);
        return ResponseEntity.noContent().build();
    }

}
