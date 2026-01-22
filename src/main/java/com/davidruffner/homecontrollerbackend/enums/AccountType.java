package com.davidruffner.homecontrollerbackend.enums;

import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public enum AccountType {
    ADMIN("admin"),
    STANDARD("standard");

    private final String value;
    private static final Map<String, AccountType> strMap = Map.ofEntries(
        entry("admin", ADMIN),
        entry("standard", STANDARD)
    );

    AccountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<AccountType> fromStrVal(String strVal) {
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
