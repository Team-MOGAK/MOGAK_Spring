package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;

public interface ModaratService {
    Modarat create(Long userId, ModaratRequestDto.CreateModaratDto request);
}
