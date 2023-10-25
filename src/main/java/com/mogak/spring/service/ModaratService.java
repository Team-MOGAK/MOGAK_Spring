package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import com.mogak.spring.web.dto.ModaratDto.ModaratRequestDto;
import com.mogak.spring.web.dto.ModaratDto.ModaratResponseDto;

import java.util.List;

public interface ModaratService {
    Modarat create(Long userId, ModaratRequestDto.CreateModaratDto request);
    void delete(Long modaratId);
    Modarat update(Long modaratId, ModaratRequestDto.UpdateModaratDto request);
    SingleDetailModaratDto getDetailModarat(Long modaratId);
    List<ModaratResponseDto.GetModaratTitleDto> getModaratTitleList(Long userId);
}
