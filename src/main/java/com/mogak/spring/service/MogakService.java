package com.mogak.spring.service;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;

import java.util.List;

public interface MogakService {
    MogakResponseDto.GetMogakDto create(Long userId, MogakRequestDto.CreateDto createDto);
    Mogak achieveMogak(Long id);
    MogakResponseDto.UpdateStateDto updateMogak(MogakRequestDto.UpdateDto request);
    MogakResponseDto.GetMogakListDto getMogakDtoList(Long userId, Long modaratId);
    void deleteMogak(Long mogakId);

    List<JogakResponseDto.GetJogakDto> getJogaks(Long mogakId);
//    List<Mogak> getOngoingTodayMogakList(int name);
//    void judgeMogakByDay(LocalDate day);
}
