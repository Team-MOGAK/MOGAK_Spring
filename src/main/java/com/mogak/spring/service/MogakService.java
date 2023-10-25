package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.MogakRequestDto;
import com.mogak.spring.web.dto.MogakResponseDto;

import java.util.List;

public interface MogakService {
    MogakResponseDto.CreateDto create(Long userId, MogakRequestDto.CreateDto createDto);
    Mogak achieveMogak(Long id);
    Mogak updateMogak(MogakRequestDto.UpdateDto request);
    List<Mogak> getMogakList(Long userId, int cursor, int size);
    void deleteMogak(Long mogakId);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}
