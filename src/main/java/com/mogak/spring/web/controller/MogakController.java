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

import java.util.List;

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

    /**
     * 모각 수정 API
     * */
    @PutMapping("")
    public ResponseEntity<MogakResponseDto.updateStateDto> updateMogak(@RequestBody MogakRequestDto.UpdateDto request) {
        Mogak mogak = mogakService.updateMogak(request);
        return ResponseEntity.status(HttpStatus.OK).body(MogakConverter.toUpdateDto(mogak));
    }

    /**
     * 모각 조회 API
     * user => 유저 PK
     * cursor => 데이터 조회 시작점
     * size => 조회할 데이터 개수
     * */
    @GetMapping("")
    public ResponseEntity<MogakResponseDto.getMogakListDto> getMogakList(
            @RequestParam(value = "user") Long userId,
            @RequestParam(value = "cursor") int cursor,
            @RequestParam(value = "size") int size) {
            List<Mogak> mogaks = mogakService.getMogakList(userId, cursor, size);
            return ResponseEntity.status(HttpStatus.OK).body(MogakConverter.toGetMogakListDto(mogaks));
    }

    /**
     * 모각 삭제 API
     * */
    @DeleteMapping("/{mogakId}")
    public ResponseEntity<Void> deleteMogak(
            @PathVariable Long mogakId) {
        mogakService.deleteMogak(mogakId);
        return ResponseEntity.noContent().build();
    }
}
