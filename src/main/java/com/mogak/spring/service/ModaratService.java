package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;

import java.util.List;

import static com.mogak.spring.web.dto.modaratdto.ModaratResponseDto.ModaratDto;

public interface ModaratService {
    Modarat create(ModaratRequestDto.CreateModaratDto request);
    void delete(Long modaratId);
    Modarat update(Long modaratId, ModaratRequestDto.UpdateModaratDto request);
    SingleDetailModaratDto getDetailModarat(Long modaratId);
    List<ModaratDto> getModaratList();
}
