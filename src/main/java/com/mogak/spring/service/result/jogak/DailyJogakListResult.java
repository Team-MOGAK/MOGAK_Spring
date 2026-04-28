package com.mogak.spring.service.result.jogak;

import java.util.List;

public record DailyJogakListResult(int size, List<DailyJogakResult> dailyJogaks) {
    public static DailyJogakListResult from(List<DailyJogakResult> dailyJogaks) {
        return new DailyJogakListResult(dailyJogaks.size(), dailyJogaks);
    }
}
