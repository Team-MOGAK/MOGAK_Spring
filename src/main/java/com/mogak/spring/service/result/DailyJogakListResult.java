package com.mogak.spring.service.result;

import java.util.List;

public record DailyJogakListResult(int size, List<DailyJogakResult> dailyJogaks) {
}
