package com.mogak.spring.service;

import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

public interface JogakService {

    void createRoutineJogakToday();
    JogakResponseDto.CreateJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto);
    void updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    JogakResponseDto.GetJogakListDto getDailyJogaks(Long userId);
    JogakResponseDto.GetJogakListDto getRoutineTodayJogaks(Long userId);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    JogakResponseDto.startDailyJogakDto startJogak(Long jogakId);

    JogakResponseDto.successJogakDto successJogak(Long jogakId);

    void deleteJogak(Long jogakId);
}
