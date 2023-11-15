package com.mogak.spring.converter;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;

public class JogakPeriodConverter {
    public static JogakPeriod toJogakPeriod(Period period, Jogak jogak) {
        return JogakPeriod.builder()
                .period(period)
                .jogak(jogak)
                .build();
    }
}
