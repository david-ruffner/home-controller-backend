package com.davidruffner.homecontrollerbackend.utils;

import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import org.hibernate.query.range.Range;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

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
}
