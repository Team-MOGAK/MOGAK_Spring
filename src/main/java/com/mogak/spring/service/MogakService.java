package com.mogak.spring.service;

import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface MogakService {
    MogakResponseDto.GetMogakDto create(MogakRequestDto.CreateDto createDto);
//    MogakResponseDto.UpdateStateDto achieveMogak(Long id);
    MogakResponseDto.GetMogakDto updateMogak(MogakRequestDto.UpdateDto request);
    MogakResponseDto.GetMogakListDto getMogakDtoList(Long modaratId);
    void deleteMogak(Long mogakId);

    List<JogakResponseDto.GetJogakDto> getJogaks(Long mogakId, LocalDate day);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}
