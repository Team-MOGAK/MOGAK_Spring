package com.mogak.spring.service.result.jogak;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;

import java.util.List;
import java.util.Objects;

public record OneTimeJogakListResult(int size, List<OneTimeJogakResult> jogaks) {
    public static OneTimeJogakListResult of(List<Jogak> jogaks, List<DailyJogak> dailyJogaks) {
        List<OneTimeJogakResult> results = jogaks.stream()
                .map(jogak -> OneTimeJogakResult.of(jogak, hasMatchingDailyJogak(jogak, dailyJogaks)))
                .toList();
        return new OneTimeJogakListResult(results.size(), results);
    }

    private static boolean hasMatchingDailyJogak(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return dailyJogaks.stream()
                .anyMatch(dailyJogak -> Objects.equals(dailyJogak.getJogak(), jogak));
    }
}
