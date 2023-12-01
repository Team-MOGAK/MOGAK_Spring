package com.mogak.spring.repository.query;

import com.mogak.spring.domain.jogak.JogakPeriod;

import java.util.List;

public interface FutureRoutineJogakProjection {
    String getTitle();
    List<JogakPeriod> getJogakPeriods();
}
