package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;
import com.mogak.spring.web.dto.modaratdto.ModaratResponseDto;

import java.util.List;

public interface ModaratService {
    Modarat create(ModaratRequestDto.CreateModaratDto request);
    void delete(Long modaratId);
    Modarat update(Long modaratId, ModaratRequestDto.UpdateModaratDto request);
    SingleDetailModaratDto getDetailModarat(Long modaratId);
    List<ModaratResponseDto.GetModaratTitleDto> getModaratTitleList();
}
