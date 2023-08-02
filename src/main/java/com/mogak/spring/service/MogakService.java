package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.MogakRequestDto;

public interface MogakService {
    // 모각 생성
    Mogak create(MogakRequestDto.CreateDto createDto);
}
