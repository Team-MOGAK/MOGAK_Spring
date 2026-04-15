package com.mogak.spring.service;

import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface JogakService {

    void createRoutineJogakToday();
    JogakResponseDto.CreateJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto);
    JogakResponseDto.CreateJogakDto updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    JogakResponseDto.GetOneTimeJogakListDto getDailyJogaks(Long userId, LocalDate day);
    JogakResponseDto.GetDailyJogakListDto getDayJogaks(Long userId, LocalDate day);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    JogakResponseDto.JogakDailyJogakDto startJogak(Long jogakId);

    JogakResponseDto.JogakDailyJogakDto successJogak(Long dailyJogakId);

    void deleteJogak(Long jogakId);

    List<JogakResponseDto.GetRoutineJogakDto> getRoutineJogaks(Long userId, LocalDate startDay, LocalDate endDay);

    JogakResponseDto.JogakDailyJogakDto failJogak(Long dailyJogakId);

    JogakResponseDto.DetailJogakDto getJogakDetail(Long jogakId);
}
