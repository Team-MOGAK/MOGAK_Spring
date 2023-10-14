package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.Jogak;

import java.util.List;

public interface JogakService {

    void createJogakToday();
    Jogak createJogak(Long mogakId);

    List<Jogak> getDailyJogaks(Long userId);
    void failJogakAtMidnight();
//    void failJogakAtFour();

//    Jogak startJogak(Long jogakId);

//    Jogak endJogak(Long jogakId);

    void deleteJogak(Long jogakId);
}
