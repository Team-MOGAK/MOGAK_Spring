package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;

public interface MogakService {
    MogakResponseDto.CreateDto create(Long userId, MogakRequestDto.CreateDto createDto);
    Mogak achieveMogak(Long id);
    MogakResponseDto.UpdateStateDto updateMogak(MogakRequestDto.UpdateDto request);
    MogakResponseDto.GetMogakListDto getMogakDtoList(Long userId, Long modaratId);
    void deleteMogak(Long mogakId);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}
