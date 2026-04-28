package com.mogak.spring.service.result;

import java.util.List;

public record MogakListResult(List<MogakResult> mogaks, int size) {
}
