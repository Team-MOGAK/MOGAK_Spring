package com.mogak.spring.service.result;

import java.util.List;

public record OneTimeJogakListResult(int size, List<OneTimeJogakResult> jogaks) {
}
