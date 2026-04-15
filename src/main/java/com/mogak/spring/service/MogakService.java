package com.mogak.spring.service;

import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface MogakService {
    MogakResponseDto.GetMogakDto create(Long userId, MogakRequestDto.CreateDto createDto);
//    MogakResponseDto.UpdateStateDto achieveMogak(Long id);
    MogakResponseDto.GetMogakDto updateMogak(Long userId, MogakRequestDto.UpdateDto request);
    MogakResponseDto.GetMogakListDto getMogakDtoList(Long userId, Long modaratId);
    void deleteMogak(Long userId, Long mogakId);
    void deleteMogakCascadeAfterParentAuthorization(Long mogakId);

    List<JogakResponseDto.GetJogakDto> getJogaks(Long userId, Long mogakId, LocalDate day);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}
