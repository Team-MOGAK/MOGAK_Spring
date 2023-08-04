package com.mogak.spring.domain.common;

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
}
