package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.MogakRequestDto;

import java.util.List;

public interface MogakService {
    Mogak create(MogakRequestDto.CreateDto createDto);
    Mogak achieveMogak(Long id);
    Mogak updateMogak(MogakRequestDto.UpdateDto request);
    List<Mogak> getMogakList(Long userId, int cursor, int size);
    void deleteMogak(Long mogakId);
}
