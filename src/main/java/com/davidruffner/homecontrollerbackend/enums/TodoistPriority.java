package com.davidruffner.homecontrollerbackend.enums;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public enum TodoistPriority {
    HIGH("High", "p1", 4),
    MEDIUM("Medium", "p2", 3),
    LOW("Low", "p3", 2),
    NONE("None", "p4", 1);

    private final String label;
    private final String filterTerm;
    private final Integer intVal;

    private static final Map<String, TodoistPriority> labelMap = Map.ofEntries(
        entry("high", HIGH),
        entry("medium", MEDIUM),
        entry("low", LOW),
        entry("none", NONE)
    );

    private static final Map<Integer, TodoistPriority> intValMap = Map.ofEntries(
        entry(4, HIGH),
        entry(3, MEDIUM),
        entry(2, LOW),
        entry(1, NONE)
    );

    public static Optional<TodoistPriority> fromLabel(String label) {
        return Optional.ofNullable(labelMap.get(label.toLowerCase(Locale.US)));
    }

    public static TodoistPriority fromIntVal(Integer intVal) {
        return intValMap.getOrDefault(intVal, null);
    }

    TodoistPriority(String label, String filterTerm, Integer intVal) {
        this.label = label;
        this.filterTerm = filterTerm;
        this.intVal = intVal;
    }

    public String getLabel() {
        return label;
    }

    public String getFilterTerm() {
        return filterTerm;
    }

    public Integer getIntVal() {
        return intVal;
    }
}
