package com.mogak.spring.web.dto.jogakdto;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

import java.util.List;

public record DailyJogakListResponse(int size, List<DailyJogakResponse> dailyJogaks) {
    public static DailyJogakListResponse fromDailyJogaks(List<DailyJogak> dailyJogaks) {
        List<DailyJogakResponse> responses = dailyJogaks.stream()
                .map(DailyJogakResponse::from)
                .toList();
        return new DailyJogakListResponse(responses.size(), responses);
    }

    public static DailyJogakListResponse fromFutureRoutineJogaks(List<Jogak> jogaks) {
        List<DailyJogakResponse> responses = jogaks.stream()
                .map(DailyJogakResponse::fromFutureRoutineJogak)
                .toList();
        return new DailyJogakListResponse(responses.size(), responses);
    }
}
