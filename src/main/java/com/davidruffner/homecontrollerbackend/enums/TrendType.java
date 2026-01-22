package com.davidruffner.homecontrollerbackend.enums;

import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public enum TrendType {
    ONE_DAY("one-day"),
    TWO_DAY("two-day"),
    THREE_DAY("three-day"),
    FIVE_DAY("five-day"),
    SEVEN_DAY("seven-day");

    private final String value;
    private static final Map<String, TrendType> strMap = Map.ofEntries(
        entry("one-day", ONE_DAY),
        entry("two-day", TWO_DAY),
        entry("three-day", THREE_DAY),
        entry("five-day", FIVE_DAY),
        entry("seven-day", SEVEN_DAY)
    );

    TrendType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<TrendType> fromStrVal(String strVal) {
        if (!strMap.containsKey(strVal)) {
            return Optional.empty();
        } else {
            return Optional.of(strMap.get(strVal));
        }
    }


    @Override
    public String toString() {
        return this.getValue();
    }
}
