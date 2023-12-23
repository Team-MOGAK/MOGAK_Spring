package com.mogak.spring.service;

import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface JogakService {

    void createRoutineJogakToday();
    JogakResponseDto.GetJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto);
    void updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    JogakResponseDto.GetJogakListDto getDailyJogaks(Long userId);
    JogakResponseDto.GetDailyJogakListDto getTodayJogaks(Long userId);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    JogakResponseDto.JogakDailyJogakDto startJogak(Long jogakId);

    JogakResponseDto.JogakDailyJogakDto successJogak(Long dailyJogakId);

    void deleteJogak(Long jogakId);

    List<JogakResponseDto.GetRoutineJogakDto> getRoutineJogaks(Long userId, LocalDate startDay, LocalDate endDay);

    JogakResponseDto.JogakDailyJogakDto failJogak(Long dailyJogakId);
}
