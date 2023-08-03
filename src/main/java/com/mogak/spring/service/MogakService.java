package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.MogakRequestDto;

public interface MogakService {
    Mogak create(MogakRequestDto.CreateDto createDto);
    Mogak achieveMogak(Long id);
    Mogak updateMogak(MogakRequestDto.UpdateDto request);
}
