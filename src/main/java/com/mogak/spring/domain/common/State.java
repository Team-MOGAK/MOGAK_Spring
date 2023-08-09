package com.mogak.spring.domain.common;

import java.time.LocalDate;

public enum State {
    BEFORE,ONGOING,COMPLETE,FAIL;

    public static State registerState(LocalDate start, LocalDate end, LocalDate now) throws RuntimeException {
        verifyReverseStartAnd(start, end);
        if (discriminateBeforeOrStart(start, now)) {
            return BEFORE;
        } else {
            return ONGOING;
        }
    }

    private static void verifyReverseStartAnd(LocalDate start, LocalDate end) {
        if (end == null || start.isBefore(end)) {
            return;
        }
        else if (start.isEqual(end)) {
            return;
        }
        throw new RuntimeException("시작 날짜와 끝 날짜 역전");
    }

    /**
     * 시작 날짜와 현재 날짜를 입력받아 시작전인지 오늘부터 하는 루틴인지 판별하는 메소드
     * return True -> Before, False -> Ongoing
     * 시작 날짜, 현재 날짜 역전 -> throw RuntimeException
     * */
    private static Boolean discriminateBeforeOrStart(LocalDate start, LocalDate now) {
        if (start.isEqual(now)) {
            return false;
        } else if (start.isAfter(now)) {
            return true;
        } else throw new RuntimeException("잘못 입력된 시작 날짜입니다.");
    }
}
