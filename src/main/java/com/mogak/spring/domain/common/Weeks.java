package com.mogak.spring.domain.common;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum Weeks {
    MONDAY("MON", 1),
    TUESDAY("TUE", 2),
    WEDNESDAY("WED", 3),
    THURSDAY("THU", 4),
    FRIDAY("FRI", 5),
    SATURDAY("SAT", 6),
    SUNDAY("SUN", 7);

    private final String name;
    private final int value;

    private Weeks(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public static int getTodayNum() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek.getValue();
    }

}
