package com.davidruffner.homecontrollerbackend.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class WeatherUtils {

    public static double getAvgWindSpeedFromStr(String windSpeedStr) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(windSpeedStr);
        Double addedWindSpeed = 0d;
        Integer index = 0;

        while (matcher.find()) {
            addedWindSpeed += Double.parseDouble(matcher.group(0));
            index++;
            out.println("Done");
        }

        return addedWindSpeed / index;
    }

    public static double calculateFeelsLikeTemp(
        double temperatureF,
        double humidityPercent,
        double windSpeedMph
    ) {

        if (temperatureF >= 80) {
            return heatIndex(temperatureF, humidityPercent);
        }

        if (temperatureF <= 50 && windSpeedMph >= 3) {
            return windChill(temperatureF, windSpeedMph);
        }

        // Otherwise, it just feels like the actual temp
        return temperatureF;
    }

    /**
     * Heat Index formula (NOAA)
     */
    private static double heatIndex(double t, double rh) {
        return -42.379
            + 2.04901523 * t
            + 10.14333127 * rh
            - 0.22475541 * t * rh
            - 0.00683783 * t * t
            - 0.05481717 * rh * rh
            + 0.00122874 * t * t * rh
            + 0.00085282 * t * rh * rh
            - 0.00000199 * t * t * rh * rh;
    }

    /**
     * Wind Chill formula (NOAA)
     */
    private static double windChill(double t, double v) {
        return 35.74
            + 0.6215 * t
            - 35.75 * Math.pow(v, 0.16)
            + 0.4275 * t * Math.pow(v, 0.16);
    }
}
