package com.davidruffner.homecontrollerbackend.utils;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.query.range.Range;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.davidruffner.homecontrollerbackend.enums.ShortCode.SYSTEM_EXCEPTION;

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

    // OffsetDateTime.parse("2026-01-13T06:00:00-05:00").toLocalDateTime()
    public static String to12HrFrom24Hr(String timestamp) {
        try {
            LocalDateTime dateTime = OffsetDateTime.parse(timestamp).toLocalDateTime();
            int twentyFourHour = dateTime.getHour();
            StringBuilder returnStr = new StringBuilder();

            if (twentyFourHour == 0) {
                returnStr.append("12 AM");
            } else if (twentyFourHour == 12) {
                returnStr.append("12 PM");
            } else if (twentyFourHour < 13) {
                returnStr.append(twentyFourHour)
                    .append(" AM");
            } else {
                returnStr.append(twentyFourHour - 12)
                    .append(" PM");
            }

            return returnStr.toString();
        } catch (Exception ex) {
            throw new ControllerException(String.format("Error while converting to 12HR time: '%s'",
                ex.getMessage()), ResponseCode.SYSTEM_EXCEPTION, SYSTEM_EXCEPTION.toString());
        }
    }

    public record ZDTTime(
        ZonedDateTime zdt,
        String timestamp
    ) {}

    private final static DateTimeFormatter LONG_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static String getTimestampFromZDT(ZonedDateTime zdt) {
        return zdt.format(LONG_FORMAT);
    }

    public static ZDTTime getZDTFromTimestamp(String timestamp, UserSettings userSettings) {
        ZonedDateTime zdt;

        if (timestamp.contains("T")) {
            // Has time info
            zdt = LocalDateTime.parse(timestamp)
                .atZone(ZoneId.of(userSettings.getTimeZone()));

        } else {
            // Missing time info
            zdt = LocalDate.parse(timestamp)
                .atStartOfDay(ZoneId.of(userSettings.getTimeZone()));

        }

        return new ZDTTime(zdt, zdt.format(LONG_FORMAT));
    }

    private static final String DEFAULT_TODOIST_LABEL_COLOR = "rgba(0, 0, 255, 0.15)"; // Dark blue
    private static final Map<String, String> TODOIST_LABEL_COLORS = Map.ofEntries(
        Map.entry("berry_red", "rgba(215, 48, 116, 0.65)"),
        Map.entry("red", "rgba(238, 82, 76, 0.65)"),
        Map.entry("orange", "rgba(203, 110, 29, 0.65)"),
        Map.entry("yellow", "rgba(182, 144, 39, 0.65)"),
        Map.entry("olive_green", "rgba(150, 156, 63, 0.65)"),
        Map.entry("lime_green", "rgba(101, 163, 70, 0.65)"),
        Map.entry("green", "rgba(53, 147, 39, 0.65)"),
        Map.entry("mint_green", "rgba(57, 164, 148, 0.65)"),
        Map.entry("teal", "rgba(0, 145, 171, 0.65)"),
        Map.entry("sky_blue", "rgba(22, 159, 190, 0.65)"),
        Map.entry("light_blue", "rgba(101, 137, 162, 0.65)"),
        Map.entry("blue", "rgba(34, 131, 250, 0.65)"),
        Map.entry("grape", "rgba(101, 47, 189, 0.65)"),
        Map.entry("violet", "rgba(202, 61, 232, 0.65)"),
        Map.entry("lavender", "rgba(166, 104, 138, 0.65)"),
        Map.entry("magenta", "rgba(227, 75, 146, 0.65)"),
        Map.entry("salmon", "rgba(205, 116, 112, 0.65)"),
        Map.entry("charcoal", "rgba(128, 128, 128, 0.65)"),
        Map.entry("grey", "rgba(153, 153, 153, 0.65)"),
        Map.entry("taupe", "rgba(145, 122, 106, 0.65)")
    );

    public static String getTodoistLabelColorByName(String colorName) {
        return TODOIST_LABEL_COLORS.getOrDefault(colorName, DEFAULT_TODOIST_LABEL_COLOR);
    }
}
