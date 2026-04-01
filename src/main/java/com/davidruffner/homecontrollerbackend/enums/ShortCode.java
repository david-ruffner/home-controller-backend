package com.davidruffner.homecontrollerbackend.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ShortCode {
    INVALID_CONTROLLER_ID("INVALID_CONTROLLER_ID"),
    SYSTEM_EXCEPTION("SYSTEM_EXCEPTION"),
    NON_EXISTENT_USER("NON_EXISTENT_USER"),
    SUCCESS("SUCCESS"),
    INVALID_TREND_TYPE("INVALID_TREND_TYPE"),
    PAGINATION_EXPIRED("PAGINATION_EXPIRED"),
    NO_TASKS("NO_TASKS"),
    BAD_REQUEST("BAD_REQUEST"),
    NO_ROOM_ITEMS("NO_ROOM_ITEMS"),
    NO_ROOMS("NO_ROOMS"),
    NO_ITEM_CONTAINERS("NO_ITEM_CONTAINERS"),
    NO_CATEGORIES("NO_CATEGORIES"),
    NO_ITEMS_FOR_CATEGORY("NO_ITEMS_FOR_CATEGORY");

    private final String name;

    private static final Map<String, ShortCode> strMap =
        Arrays.stream(values())
            .collect(Collectors.toMap(
                ShortCode::getName,
                Function.identity()
            ));

    ShortCode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<ShortCode> fromName(String name) {
        if (strMap.containsKey(name)) {
            return Optional.of(strMap.get(name));
        } else {
            return Optional.empty();
        }
    }


    @Override
    public String toString() {
        return this.name;
    }
}
