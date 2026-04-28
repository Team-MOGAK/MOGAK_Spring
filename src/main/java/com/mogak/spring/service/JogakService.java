package com.mogak.spring.service;

import com.mogak.spring.service.command.CreateJogakCommand;
import com.mogak.spring.service.command.UpdateJogakCommand;
import com.mogak.spring.service.result.CreateJogakResult;
import com.mogak.spring.service.result.DailyJogakListResult;
import com.mogak.spring.service.result.JogakDailyResult;
import com.mogak.spring.service.result.JogakDetailResult;
import com.mogak.spring.service.result.OneTimeJogakListResult;
import com.mogak.spring.service.result.RoutineJogakResult;

import java.time.LocalDate;
import java.util.List;

public interface JogakService {

    void createRoutineJogakToday();
    CreateJogakResult createJogak(Long userId, CreateJogakCommand command);
    CreateJogakResult updateJogak(Long userId, Long jogakId, UpdateJogakCommand command);
    OneTimeJogakListResult getDailyJogaks(Long userId, LocalDate day);
    DailyJogakListResult getDayJogaks(Long userId, LocalDate day);
//    void failRoutineJogakAtMidnight();
//    void failJogakAtFour();

    JogakDailyResult startJogak(Long userId, Long jogakId);

    JogakDailyResult successJogak(Long userId, Long dailyJogakId);

    void deleteJogak(Long userId, Long jogakId);
    void deleteJogakCascadeAfterParentAuthorization(Long jogakId);

    List<RoutineJogakResult> getRoutineJogaks(Long userId, LocalDate startDay, LocalDate endDay);

    JogakDailyResult failJogak(Long userId, Long dailyJogakId);

    JogakDetailResult getJogakDetail(Long userId, Long jogakId);
}
