package com.davidruffner.homecontrollerbackend.utils;

import org.hibernate.query.range.Range;

import java.time.ZonedDateTime;

public class Utils {

    public static boolean strNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean strEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public record ZonedRange(ZonedDateTime start, ZonedDateTime end) {}

    public static ZonedRange dayBounds(ZonedDateTime zdt) {
        ZonedDateTime start = zdt.toLocalDate().atStartOfDay(zdt.getZone());
        ZonedDateTime end = start.plusDays(1).minusNanos(1);

        return new ZonedRange(start, end);
    }

    public static ZonedRange dayBoundsSameDay(ZonedDateTime zdt) {
        ZonedDateTime start = zdt.toLocalDate().atStartOfDay(zdt.getZone());
        ZonedDateTime end = start
            .plusHours(23)
            .plusMinutes(59)
            .plusSeconds(59)
            .plusNanos(999);

        return new ZonedRange(start, end);
    }
}
