package com.mogak.spring.service;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.service.result.ModaratDetailResult;
import com.mogak.spring.service.result.ModaratSummaryResult;

import java.util.List;

public interface ModaratService {
    Modarat create(Long userId, String title, String color);
    void delete(Long userId, Long modaratId);
    Modarat update(Long userId, Long modaratId, String title, String color);
    ModaratDetailResult getDetailModarat(Long userId, Long modaratId);
    List<ModaratSummaryResult> getModaratList(Long userId);
}
