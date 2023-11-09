package com.mogak.spring.service;

import com.mogak.spring.web.dto.JogakRequestDto;
import com.mogak.spring.web.dto.JogakResponseDto;

public interface JogakService {

    void createRoutineJogakToday();
    JogakResponseDto.CreateJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto);
    void updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    JogakResponseDto.GetJogakListDto getDailyJogaks(Long userId);
    JogakResponseDto.GetJogakListDto getRoutineTodayJogaks(Long userId);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    void startJogak(Long jogakId);

    JogakResponseDto.successJogakDto successJogak(Long jogakId);

    void deleteJogak(Long jogakId);
}
