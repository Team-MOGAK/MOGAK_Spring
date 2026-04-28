package com.mogak.spring.web.dto.jogakdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

import java.util.List;

public record OneTimeJogakListResponse(int size, List<OneTimeJogakResponse> jogaks) {
    public static OneTimeJogakListResponse from(List<Jogak> jogaks, List<DailyJogak> dailyJogaks) {
        List<OneTimeJogakResponse> responses = jogaks.stream()
                .map(jogak -> OneTimeJogakResponse.from(jogak, dailyJogaks))
                .toList();
        return new OneTimeJogakListResponse(responses.size(), responses);
    }
}
