package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.web.dto.JogakRequestDto;

public interface JogakService {

    void createJogakByScheduler();
    Jogak createJogak(Long mogakId);
}
