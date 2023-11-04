package com.mogak.spring.service;

import com.mogak.spring.web.dto.JogakRequestDto;
import com.mogak.spring.web.dto.JogakResponseDto;

public interface JogakService {

    void createJogakToday();
    JogakResponseDto.CreateJogakDto createJogak(JogakRequestDto.CreateJogakDto createJogakDto);
    void updateJogak(Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    JogakResponseDto.GetJogakListDto getDailyJogaks(Long userId);
    void failJogakAtMidnight();
//    void failJogakAtFour();

//    Jogak startJogak(Long jogakId);

//    Jogak endJogak(Long jogakId);

    void deleteJogak(Long jogakId);
}
