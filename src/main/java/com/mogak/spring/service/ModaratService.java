package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.service.result.modarat.ModaratDetailResult;
import com.mogak.spring.service.result.modarat.ModaratResult;
import com.mogak.spring.web.dto.modaratdto.ModaratRequestDto;

import java.util.List;

public interface ModaratService {
    Modarat create(Long userId, ModaratRequestDto.CreateModaratDto request);
    void delete(Long userId, Long modaratId);
    Modarat update(Long userId, Long modaratId, ModaratRequestDto.UpdateModaratDto request);
    ModaratDetailResult getDetailModarat(Long userId, Long modaratId);
    List<ModaratResult> getModaratList(Long userId);
}
