package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.web.dto.JogakRequestDto;

import java.util.List;

public interface JogakService {

    void createJogakByScheduler();
    Jogak createJogak(Long mogakId);

    List<Jogak> getDailyJogaks(Long userId);

    Jogak startJogak(Long jogakId);
}
