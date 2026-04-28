package com.mogak.spring.service;

import com.mogak.spring.service.result.jogak.CreateJogakResult;
import com.mogak.spring.service.result.jogak.DailyJogakListResult;
import com.mogak.spring.service.result.jogak.DetailJogakResult;
import com.mogak.spring.service.result.jogak.JogakDailyJogakResult;
import com.mogak.spring.service.result.jogak.OneTimeJogakListResult;
import com.mogak.spring.service.result.jogak.RoutineJogakResult;
import com.mogak.spring.web.dto.jogakdto.JogakRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface JogakService {

    void createRoutineJogakToday();
    CreateJogakResult createJogak(Long userId, JogakRequestDto.CreateJogakDto createJogakDto);
    CreateJogakResult updateJogak(Long userId, Long jogakId, JogakRequestDto.UpdateJogakDto updateJogakDto);
    OneTimeJogakListResult getDailyJogaks(Long userId, LocalDate day);
    DailyJogakListResult getDayJogaks(Long userId, LocalDate day);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    JogakDailyJogakResult startJogak(Long userId, Long jogakId);

    JogakDailyJogakResult successJogak(Long userId, Long dailyJogakId);

    void deleteJogak(Long userId, Long jogakId);
    void deleteJogakCascadeAfterParentAuthorization(Long jogakId);

    List<RoutineJogakResult> getRoutineJogaks(Long userId, LocalDate startDay, LocalDate endDay);

    JogakDailyJogakResult failJogak(Long userId, Long dailyJogakId);

    DetailJogakResult getJogakDetail(Long userId, Long jogakId);
}
