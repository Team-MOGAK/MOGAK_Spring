package com.mogak.spring.service;

import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface JogakService {

    void createRoutineJogakToday();
    JogakResponseDto.CreateJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto);
    void updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    JogakResponseDto.GetJogakListDto getDailyJogaks(Long userId);
    JogakResponseDto.GetJogakListDto getTodayRoutineJogaks(Long userId);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    JogakResponseDto.startDailyJogakDto startJogak(Long jogakId);

    JogakResponseDto.JogakSuccessDto successJogak(Long jogakId);

    void deleteJogak(Long jogakId);

    List<JogakResponseDto.GetRoutineJogakDto> getRoutineJogaks(Long userId, LocalDate startDay, LocalDate endDay);
}
