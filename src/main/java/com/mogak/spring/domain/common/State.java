package com.mogak.spring.domain.common;

import java.time.LocalDate;

public enum State {
    BEFORE,ONGOING,COMPLETE;

    public static State registerState(LocalDate startDate, LocalDate now) {
        if (startDate.isEqual(now)) {
            return ONGOING;
        } else if (startDate.isAfter(now)) {
            return BEFORE;
        } else throw new RuntimeException("잘못 입력된 시작 날짜입니다.");
    }
}
