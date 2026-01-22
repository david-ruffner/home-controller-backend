package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.enums.TrendType;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.utils.Utils;
import com.davidruffner.homecontrollerbackend.utils.Utils.ZonedRange;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.davidruffner.homecontrollerbackend.enums.ShortCode.SYSTEM_EXCEPTION;
import static com.davidruffner.homecontrollerbackend.utils.Utils.*;
import static com.davidruffner.homecontrollerbackend.utils.WeatherUtils.calculateFeelsLikeTemp;
import static com.davidruffner.homecontrollerbackend.utils.WeatherUtils.getAvgWindSpeedFromStr;
import static java.util.Map.entry;

@Service
public class WeatherService {

    @Autowired
    @Qualifier("NWSRestClient")
    RestClient nwsRestController;

    public record NWSEndpointProperties(
        String forecast
    ) {}

    public record NWSEndpointResponse(
        NWSEndpointProperties properties
    ) {}

    // -------------------------------------------------------

    public static class NWSForecastPeriod {
        private final Integer number;
        private final Instant startTime;
        private final Instant endTime;
        private final String name;
        private final Boolean isDaytime;
        private final Integer temperature;
        private final String temperatureUnit;
        private final NWSForecastProbablyOfPrecip probablyOfPrecipitation;
        private final String windSpeed;
        private final String windDirection;
        private final String shortForecast;
        private final String detailedForecast;

        private Double relativeHumidity;
        private Integer feelsLikeTemp;
        private ZonedDateTime zonedStartTime;
        private ZonedDateTime zonedEndTime;
        private final String windStr;

        public NWSForecastPeriod(Integer number, String name, Integer temperature, String temperatureUnit,
            NWSForecastProbablyOfPrecip probablyOfPrecipitation, String windSpeed, String windDirection,
            String shortForecast, String detailedForecast, Instant startTime, Instant endTime,
            Boolean isDaytime) {
            this.number = number;
            this.name = name;
            this.temperature = temperature;
            this.temperatureUnit = temperatureUnit;
            this.probablyOfPrecipitation = probablyOfPrecipitation;
            this.windSpeed = windSpeed;
            this.windDirection = windDirection;
            this.shortForecast = shortForecast;
            this.detailedForecast = detailedForecast;
            this.startTime = startTime;
            this.endTime = endTime;
            this.isDaytime = isDaytime;
            this.windStr = this.windDirection + " @ " + this.windSpeed;
        }

        public Integer getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public Integer getTemperature() {
            return temperature;
        }

        public String getTemperatureUnit() {
            return temperatureUnit;
        }

        public String getTemperatureStr() {
            return new StringBuilder()
                .append(this.getTemperature())
                .append(" ")
                .append(this.getTemperatureUnit())
                .append("°")
                .toString();
        }

        public NWSForecastProbablyOfPrecip getProbablyOfPrecipitation() {
            return probablyOfPrecipitation;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public String getWindDirection() {
            return windDirection;
        }

        public String getWindStr() {
            return this.windStr;
        }

        public String getShortForecast() {
            return shortForecast;
        }

        public String getDetailedForecast() {
            return detailedForecast;
        }

        public Instant getStartTime() {
            return startTime;
        }

        public Instant getEndTime() {
            return endTime;
        }

        public Integer getRelativeHumidity() {
            return Math.toIntExact(Math.round(this.relativeHumidity));
        }

        public void setRelativeHumidity(Double relativeHumidity) {
            this.relativeHumidity = relativeHumidity;
        }

        public Integer getFeelsLikeTemp() {
            return feelsLikeTemp;
        }

        @JsonProperty(value = "feelsLikeTempStr")
        public String getFeelsLikeTempStr() {
            return this.feelsLikeTemp + " F°";
        }

        public void setFeelsLikeTemp(Double feelsLikeTemp) {
            this.feelsLikeTemp = Math.toIntExact(Math.round(feelsLikeTemp));
        }

        public Boolean getIsDaytime() {
            return isDaytime;
        }

        public ZonedDateTime getZonedStartTime() {
            return zonedStartTime;
        }

        public NWSForecastPeriod setZonedStartTime(String timeZone) {
            this.zonedStartTime = ZonedDateTime.ofInstant(this.startTime, ZoneId.of(timeZone));
            return this;
        }

        public ZonedDateTime getZonedEndTime() {
            return zonedEndTime;
        }

        public NWSForecastPeriod setZonedEndTime(String timeZone) {
            this.zonedEndTime = ZonedDateTime.ofInstant(this.endTime, ZoneId.of(timeZone));
            return this;
        }
    }

    public record NWSForecastProbablyOfPrecip(
        Integer value
    ) {}

    public record NWSForecastProperties(
        List<NWSForecastPeriod> periods
    ) {}

    public record NWSForecastResponse(
        NWSForecastProperties properties,
        Boolean status,
        String errMsg
    ) {}

    public record NWSHourlyRelativeHumidity(
        Integer value
    ) {}

    public record NWSHourlyProbOfPrecip(
        Integer value
    ) {}

    public static class NWSHourlyPeriod {
        private final Integer number;
        private final Instant startTime;
        private final Instant endTime;
        private final Double temperature;
        private final NWSHourlyRelativeHumidity relativeHumidity;
        private final NWSHourlyProbOfPrecip probabilityOfPrecipitation;
        private final String windSpeed;
        private final String windDirection;
        private final String shortForecast;

        private final Integer feelsLikeTemp;
        private final String feelsLikeTempStr;
        private final String windStr;
        private final String temperatureStr;
        private final String humidityStr; // Includes percent sign as string
        private final String probOfPrecipStr; // Includes percent sign as string
        private ZonedDateTime zonedStartTime;
        private ZonedDateTime zonedEndTime;

        public NWSHourlyPeriod(Integer number, Instant startTime, Instant endTime, Double temperature,
            NWSHourlyRelativeHumidity relativeHumidity, NWSHourlyProbOfPrecip probabilityOfPrecipitation,
            String windSpeed, String windDirection, String shortForecast) {

            this.number = number;
            this.startTime = startTime;
            this.endTime = endTime;
            this.temperature = temperature;
            this.relativeHumidity = relativeHumidity;
            this.probabilityOfPrecipitation = probabilityOfPrecipitation;
            this.windSpeed = windSpeed;
            this.windDirection = windDirection;
            this.shortForecast = shortForecast;

            this.feelsLikeTemp = Math.toIntExact(Math.round(calculateFeelsLikeTemp(temperature,
                relativeHumidity.value, getAvgWindSpeedFromStr(windSpeed))));
            this.windStr = windDirection + " @ " + windSpeed;
            this.temperatureStr = Math.toIntExact(Math.round(this.temperature)) + " F°";
            this.feelsLikeTempStr = this.feelsLikeTemp + " F°";
            this.humidityStr = this.relativeHumidity.value + "%";
            this.probOfPrecipStr = this.probabilityOfPrecipitation.value + "%";
        }

        public Integer getNumber() {
            return number;
        }

        public Instant getStartTime() {
            return startTime;
        }

        public Instant getEndTime() {
            return endTime;
        }

        public Double getTemperature() {
            return temperature;
        }

        public Integer getTemperatureInt() {
            return Math.toIntExact(Math.round(this.temperature));
        }

        public NWSHourlyRelativeHumidity getRelativeHumidity() {
            return relativeHumidity;
        }

        public NWSHourlyProbOfPrecip getProbabilityOfPrecipitation() {
            return probabilityOfPrecipitation;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public String getWindDirection() {
            return windDirection;
        }

        public String getShortForecast() {
            return shortForecast;
        }

        public Integer getFeelsLikeTemp() {
            return feelsLikeTemp;
        }

        public String getWindStr() {
            return windStr;
        }

        public String getTemperatureStr() {
            return temperatureStr;
        }

        public ZonedDateTime getZonedStartTime() {
            return zonedStartTime;
        }

        public void setZonedStartTime(String timeZone) {
            this.zonedStartTime = this.startTime.atZone(ZoneId.of(timeZone));
        }

        public ZonedDateTime getZonedEndTime() {
            return zonedEndTime;
        }

        public void setZonedEndTime(String timeZone) {
            this.zonedEndTime = this.endTime.atZone(ZoneId.of(timeZone));
        }

        public String getFeelsLikeTempStr() {
            return feelsLikeTempStr;
        }

        public String getHumidityStr() {
            return humidityStr;
        }

        public String getProbOfPrecipStr() {
            return probOfPrecipStr;
        }
    }

    public record NWSHourlyProperties(
        List<NWSHourlyPeriod> periods
    ) {}

    public record NWSHourlyResponse(
        NWSHourlyProperties properties
    ) {}

    private String getForecastAPI(String lat, String lon) {
        String shortenedLat = "";
        String shortenedLon = "";

        if (lat.contains("-")) {
            shortenedLat = lat.substring(0, 8);
        } else {
            shortenedLat = lat.substring(0, 7);
        }

        if (lon.contains("-")) {
            shortenedLon = lon.substring(0, 8);
        } else {
            shortenedLon = lon.substring(0, 7);
        }

        URI uri = UriComponentsBuilder
            .fromPath("/points/" + shortenedLat + "," + shortenedLon)
            .build()
            .toUri();

        NWSEndpointResponse endpointResponse = this.nwsRestController
            .get()
            .uri(uri)
            .retrieve()
            .body(NWSEndpointResponse.class);

        return endpointResponse.properties.forecast.split("https://api.weather.gov")[1];
    }

    public NWSHourlyPeriod getCurrentConditions(String lat, String lon) {
        Instant currentTime = Instant.now()
            .atZone(ZoneId.of("America/Detroit"))
            .truncatedTo(ChronoUnit.HOURS)
            .toInstant();
        Instant inOneHour = Instant.now()
            .atZone(ZoneId.of("America/Detroit"))
            .truncatedTo(ChronoUnit.HOURS)
            .toInstant()
            .plus(1, ChronoUnit.HOURS);

        String forecastAPI = getForecastAPI(lat, lon);
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        NWSHourlyPeriod hourlyPeriod = hourlyPeriods.stream()
            .filter(i -> i.startTime.isAfter(currentTime.minusSeconds(5))
                && i.endTime.isBefore(inOneHour.plusSeconds(5)))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Current time not found for NWS report"));

        return hourlyPeriod;
    }

    public NWSForecastResponse getForecastResponse(String lat, String lon) {
        String forecastAPI = getForecastAPI(lat, lon);
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        NWSForecastResponse forecastResponse = this.nwsRestController
            .get()
            .uri(forecastAPI)
            .retrieve()
            .body(NWSForecastResponse.class);

        forecastResponse.properties.periods.forEach(fp -> {
            AtomicReference<Double> addedHumidities = new AtomicReference<>(0d);
            AtomicReference<Integer> totalCount = new AtomicReference<>(0);

            hourlyPeriods.stream()
                .filter(i -> i.startTime.isAfter(fp.getStartTime().minusSeconds(5))
                    && i.endTime.isBefore(fp.getEndTime().plusSeconds(5)))
                .map(NWSHourlyPeriod::getRelativeHumidity)
                .map(NWSHourlyRelativeHumidity::value)
                .forEach(h -> {
                    addedHumidities.set(addedHumidities.get() + h);
                    totalCount.set(totalCount.get() + 1);
                });

            Double averageHumidity = addedHumidities.get() / totalCount.get();
            fp.setRelativeHumidity(averageHumidity);

            Double feelsLikeTemp = calculateFeelsLikeTemp(fp.temperature, averageHumidity,
                getAvgWindSpeedFromStr(fp.windSpeed));
            fp.setFeelsLikeTemp(feelsLikeTemp);
        });

        return forecastResponse;
    }

    public static class GetNighttimeForecastResponse {
        private final NWSForecastPeriod forecastPeriod;
        private final List<NWSHourlyPeriod> nighttimePeriods;

        public GetNighttimeForecastResponse(NWSForecastPeriod forecastPeriod, List<NWSHourlyPeriod> nighttimePeriods) {
            this.forecastPeriod = forecastPeriod;
            this.nighttimePeriods = nighttimePeriods;
        }

        public NWSForecastPeriod getForecastPeriod() {
            return forecastPeriod;
        }

        public List<NWSHourlyPeriod> getNighttimePeriods() {
            return nighttimePeriods;
        }
    }

    public static class GetDaytimeForecastResponse {
        private final NWSForecastPeriod forecastPeriod;
        private final List<NWSHourlyPeriod> daytimePeriods;
        private final String errCode;

        public GetDaytimeForecastResponse(NWSForecastPeriod forecastPeriod, List<NWSHourlyPeriod> daytimePeriods) {
            this.forecastPeriod = forecastPeriod;
            this.daytimePeriods = daytimePeriods;
            this.errCode = null;
        }

        public GetDaytimeForecastResponse(String errCode) {
            this.forecastPeriod = null;
            this.daytimePeriods = null;
            this.errCode = errCode;
        }

        public NWSForecastPeriod getForecastPeriod() {
            return forecastPeriod;
        }

        public List<NWSHourlyPeriod> getDaytimePeriods() {
            return daytimePeriods;
        }

        public String getErrCode() {
            return errCode;
        }
    }

    public static class GetTomorrowForecastResponse {
        private final NWSForecastPeriod forecastPeriod;
        private final List<NWSHourlyPeriod> allPeriods;
        private final String errCode;

        public GetTomorrowForecastResponse(NWSForecastPeriod forecastPeriod, List<NWSHourlyPeriod> allPeriods) {
            this.forecastPeriod = forecastPeriod;
            this.allPeriods = allPeriods;
            this.errCode = null;
        }

        public GetTomorrowForecastResponse(String errCode) {
            this.forecastPeriod = null;
            this.allPeriods = null;
            this.errCode = errCode;
        }

        public NWSForecastPeriod getForecastPeriod() {
            return forecastPeriod;
        }

        public List<NWSHourlyPeriod> getAllPeriods() {
            return allPeriods;
        }

        public String getErrCode() {
            return errCode;
        }
    }

    public GetTomorrowForecastResponse getTomorrowForecastResponse(UserSettings userSettings) throws Exception {
        Instant now = Instant.now();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(now, ZoneId.of(userSettings.getTimeZone())).plusDays(1);
        ZonedRange zonedRange = dayBounds(zdt);
        String tomorrowName = now
            .atZone(ZoneId.of(userSettings.getTimeZone()))
            .plusDays(1)
            .getDayOfWeek()
            .getDisplayName(TextStyle.FULL, Locale.US);

        NWSForecastResponse forecastResponse = getForecastResponse(userSettings.getLat(), userSettings.getLon());
        NWSForecastPeriod forecastPeriod = forecastResponse.properties.periods.stream()
            .filter(p -> p.getName().equals(tomorrowName))
            .findFirst()
            .orElseThrow(() -> new ControllerException("Can't find any tomorrow's periods", ResponseCode.SYSTEM_EXCEPTION,
                SYSTEM_EXCEPTION.toString()));

        String forecastAPI = getForecastAPI(userSettings.getLat(), userSettings.getLon());
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        List<NWSHourlyPeriod> nighttimePeriods = hourlyPeriods.stream()
            .filter(i -> i.startTime.isAfter(zonedRange.start().toInstant().minusSeconds(1))
                && i.endTime.isBefore(zonedRange.end().toInstant().plusSeconds(1)))
            .toList();
        nighttimePeriods.forEach(p -> {
            p.setZonedStartTime(userSettings.getTimeZone());
            p.setZonedEndTime(userSettings.getTimeZone());
        });

        return new GetTomorrowForecastResponse(forecastPeriod, nighttimePeriods);
    }

    public GetNighttimeForecastResponse getNighttimeForecast(UserSettings userSettings) throws Exception {
        NWSForecastResponse forecastResponse = getForecastResponse(userSettings.getLat(), userSettings.getLon());
        NWSForecastPeriod forecastPeriod = forecastResponse.properties.periods.stream()
            .filter(p -> !p.isDaytime)
            .findFirst()
            .orElseThrow(() -> new Exception("Can't find any nighttime periods"));

        String forecastAPI = getForecastAPI(userSettings.getLat(), userSettings.getLon());
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        List<NWSHourlyPeriod> nighttimePeriods = hourlyPeriods.stream()
            .filter(i -> i.startTime.isAfter(forecastPeriod.startTime.minusSeconds(5))
                && i.endTime.isBefore(forecastPeriod.endTime.plusSeconds(5)))
            .toList();
        nighttimePeriods.forEach(p -> {
            p.setZonedStartTime(userSettings.getTimeZone());
            p.setZonedEndTime(userSettings.getTimeZone());
        });

        return new GetNighttimeForecastResponse(forecastPeriod, nighttimePeriods);
    }

    public GetDaytimeForecastResponse getDaytimeForecast(UserSettings userSettings) {
        NWSForecastResponse forecastResponse = getForecastResponse(userSettings.getLat(), userSettings.getLon());
        int currentDayOfMonth = Instant.now().atZone(ZoneId.of(userSettings.getTimeZone()))
            .toLocalDateTime().getDayOfMonth();
        Optional<NWSForecastPeriod> forecastPeriodOpt = forecastResponse.properties.periods.stream()
            .filter(p -> p.isDaytime && p.getStartTime().atZone(ZoneId.of(userSettings.getTimeZone()))
                .toLocalDateTime().getDayOfMonth() == currentDayOfMonth
            && p.getEndTime().atZone(ZoneId.of(userSettings.getTimeZone()))
                .toLocalDateTime().getDayOfMonth() == currentDayOfMonth)
            .findFirst();

        if (forecastPeriodOpt.isEmpty()) {
            return new GetDaytimeForecastResponse("FORECAST_EXHAUSTED");
        }
        NWSForecastPeriod forecastPeriod = forecastPeriodOpt.get();

        // Get hourly forecasts within the bounds of the forecastPeriod
        String forecastAPI = getForecastAPI(userSettings.getLat(), userSettings.getLon());
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        List<NWSHourlyPeriod> daytimePeriods = hourlyPeriods.stream()
            .filter(i -> i.startTime.isAfter(forecastPeriod.startTime.minusSeconds(5))
                && i.endTime.isBefore(forecastPeriod.endTime.plusSeconds(5)))
            .toList();
        daytimePeriods.forEach(p -> {
            p.setZonedStartTime(userSettings.getTimeZone());
            p.setZonedEndTime(userSettings.getTimeZone());
        });

        return new GetDaytimeForecastResponse(forecastPeriod, daytimePeriods);
    }

    public static class GetTodayForecastResponse {
        private final NWSForecastPeriod forecastPeriod;
        private final List<NWSHourlyPeriod> allPeriods;
        private final String errCode;

        public GetTodayForecastResponse(NWSForecastPeriod forecastPeriod, List<NWSHourlyPeriod> allPeriods) {
            this.forecastPeriod = forecastPeriod;
            this.allPeriods = allPeriods;
            this.errCode = null;
        }

        public GetTodayForecastResponse(String errCode) {
            this.errCode = errCode;
            this.forecastPeriod = null;
            this.allPeriods = null;
        }

        public NWSForecastPeriod getForecastPeriod() {
            return forecastPeriod;
        }

        public List<NWSHourlyPeriod> getAllPeriods() {
            return allPeriods;
        }

        public String getErrCode() {
            return errCode;
        }
    }

    public GetTodayForecastResponse getTodayForecastResponse(UserSettings userSettings) {
        Instant now = Instant.now();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(now, ZoneId.of(userSettings.getTimeZone()));
        ZonedRange zonedRange = dayBoundsSameDay(zdt);
        String todayName = now
            .atZone(ZoneId.of(userSettings.getTimeZone()))
            .getDayOfWeek()
            .getDisplayName(TextStyle.FULL, Locale.US);

        NWSForecastResponse forecastResponse = getForecastResponse(userSettings.getLat(), userSettings.getLon());
        NWSForecastPeriod forecastPeriod = forecastResponse.properties.periods.stream()
            .filter(p -> p.getName().equals(todayName) || p.getName().equals("Today"))
            .findFirst()
            .orElseThrow(() -> new ControllerException("Can't find any today's periods", ResponseCode.SYSTEM_EXCEPTION,
                SYSTEM_EXCEPTION.toString()));

        String forecastAPI = getForecastAPI(userSettings.getLat(), userSettings.getLon());
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        List<NWSHourlyPeriod> todayPeriods = hourlyPeriods.stream()
            .filter(i -> i.startTime.isAfter(zonedRange.start().toInstant().minusSeconds(1))
                && i.endTime.isBefore(zonedRange.end().toInstant().plusSeconds(1)))
            .toList();
        todayPeriods.forEach(p -> {
            p.setZonedStartTime(userSettings.getTimeZone());
            p.setZonedEndTime(userSettings.getTimeZone());
        });

        return new GetTodayForecastResponse(forecastPeriod, todayPeriods);
    }

    public static class SingleDayForecastResponse {
        private NWSForecastPeriod daytimePeriod;
        private NWSForecastPeriod nighttimePeriod;

        public SingleDayForecastResponse(NWSForecastPeriod daytimePeriod, NWSForecastPeriod nighttimePeriod) {
            this.daytimePeriod = daytimePeriod;
            this.nighttimePeriod = nighttimePeriod;
        }

        public SingleDayForecastResponse() {};

        public NWSForecastPeriod getDaytimePeriod() {
            return daytimePeriod;
        }

        public SingleDayForecastResponse setDaytimePeriod(NWSForecastPeriod daytimePeriod) {
            this.daytimePeriod = daytimePeriod;
            return this;
        }

        public NWSForecastPeriod getNighttimePeriod() {
            return nighttimePeriod;
        }

        public SingleDayForecastResponse setNighttimePeriod(NWSForecastPeriod nighttimePeriod) {
            this.nighttimePeriod = nighttimePeriod;
            return this;
        }
    }

    private static Map<String, SingleDayForecastResponse> getPeriodsMappedByDay(List<NWSForecastPeriod> forecastPeriods) {
        Map<String, SingleDayForecastResponse> singleDayPeriodMap = new LinkedHashMap<>();

        Map<String, String> acceptableKeyNames = Map.ofEntries(
            entry("Sunday", "Sunday"),
            entry("Sunday Night", "Sunday Night"),
            entry("Monday", "Monday"),
            entry("Monday Night", "Monday Night"),
            entry("Tuesday", "Tuesday"),
            entry("Tuesday Night", "Tuesday Night"),
            entry("Wednesday", "Wednesday"),
            entry("Wednesday Night", "Wednesday Night"),
            entry("Thursday", "Thursday"),
            entry("Thursday Night", "Thursday Night"),
            entry("Friday", "Friday"),
            entry("Friday Night", "Friday Night"),
            entry("Saturday", "Saturday"),
            entry("Saturday Night", "Saturday Night")
        );

        forecastPeriods.forEach(fp -> {
            if (!acceptableKeyNames.containsKey(fp.getName())) {
                return;
            }

            String key = fp.getName().replace("Night", "").trim();
            if (singleDayPeriodMap.containsKey(key)) {
                SingleDayForecastResponse currFP = singleDayPeriodMap.get(key);

                if (fp.getIsDaytime()) {
                    currFP.setDaytimePeriod(fp);
                } else {
                    currFP.setNighttimePeriod(fp);
                }

                singleDayPeriodMap.put(key, currFP);
            } else {
                SingleDayForecastResponse newFP = new SingleDayForecastResponse();

                if (fp.getIsDaytime()) {
                    newFP.setDaytimePeriod(fp);
                } else {
                    newFP.setNighttimePeriod(fp);
                }

                singleDayPeriodMap.put(key, newFP);
            }
        });

        return singleDayPeriodMap;
    }

    public static class GetSevenDayForecastResponse {
        private final Map<String, SingleDayForecastResponse> singleDayPeriodMap;

        public GetSevenDayForecastResponse(List<NWSForecastPeriod> forecastPeriods, UserSettings userSettings) {
            this.singleDayPeriodMap = getPeriodsMappedByDay(forecastPeriods);
            sortSingleDayPeriodMapInPlace(this.singleDayPeriodMap);
        }

        public Map<String, SingleDayForecastResponse> getSingleDayPeriodMap() {
            return singleDayPeriodMap;
        }
    }

    public GetSevenDayForecastResponse getSevenDayForecast(UserSettings userSettings) {
        NWSForecastResponse forecastResponse = getForecastResponse(userSettings.getLat(), userSettings.getLon());
        List<NWSForecastPeriod> forecastPeriods = forecastResponse.properties().periods();

        return new GetSevenDayForecastResponse(forecastPeriods, userSettings);
    }

    private static DayOfWeek toDayOfWeek(String s) {
        String base = s.replace(" Night", "").trim(); // optional
        return DayOfWeek.valueOf(base.toUpperCase(Locale.US)); // "Sunday" -> "SUNDAY"
    }

    private static int sundayFirstIndex(DayOfWeek d) {
        return d.getValue() % 7;
    }

    private static Instant getEarliestStart(SingleDayForecastResponse r) {
        Instant dayStart = r.getDaytimePeriod() != null
            ? r.getDaytimePeriod().getStartTime()
            : null;

        Instant nightStart = r.getNighttimePeriod() != null
            ? r.getNighttimePeriod().getStartTime()
            : null;

        if (dayStart == null) return nightStart;
        if (nightStart == null) return dayStart;

        return dayStart.isBefore(nightStart) ? dayStart : nightStart;
    }

    private static void sortSingleDayPeriodMapInPlace(
        Map<String, SingleDayForecastResponse> map
    ) {
        LinkedHashMap<String, SingleDayForecastResponse> sorted = map.entrySet().stream()
            .sorted(Comparator.comparing(e -> getEarliestStart(e.getValue())))
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));

        map.clear();
        map.putAll(sorted);
    }

    public static class GetThreeDayForecastResponse {
        private final Map<String, SingleDayForecastResponse> singleDayPeriodMap;

        public GetThreeDayForecastResponse(List<NWSForecastPeriod> forecastPeriods, UserSettings userSettings) {
            this.singleDayPeriodMap = getPeriodsMappedByDay(forecastPeriods);
            Map<String, String> acceptableNames = new HashMap<>();

            for (int i = 1; i < 4; i++) {
                String name = Instant.now()
                    .atZone(ZoneId.of(userSettings.getTimeZone()))
                    .plusDays(i)
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.US);
                String nightName = name + " Night";
                acceptableNames.put(name, name);
                acceptableNames.put(nightName, nightName);
            }

            this.singleDayPeriodMap.keySet()
                .forEach(key -> {
                    if (!acceptableNames.containsKey(key)) {
                        this.singleDayPeriodMap.put(key, null);
                    }
                });


            // list.sort(Comparator.comparingInt(o -> sundayFirstIndex(toDayOfWeek(o.getDayName()))));
            this.singleDayPeriodMap.entrySet()
                .removeIf(entry -> entry.getValue() == null);

            sortSingleDayPeriodMapInPlace(this.singleDayPeriodMap);
        }

        public Map<String, SingleDayForecastResponse> getSingleDayPeriodMap() {
            return singleDayPeriodMap;
        }
    }

    public GetThreeDayForecastResponse getThreeDayForecast(UserSettings userSettings) {
        NWSForecastResponse forecastResponse = getForecastResponse(userSettings.getLat(), userSettings.getLon());
        List<NWSForecastPeriod> forecastPeriods = forecastResponse.properties().periods();

        return new GetThreeDayForecastResponse(forecastPeriods, userSettings);
    }

    public static class HourlyPrecipitation {
        private final ZonedDateTime zonedStartTime;
        private final ZonedDateTime zonedEndTime;
        private final String twelveHourStartTime;
        private final String twelveHourEndTime;
        private final Integer precipitationValue;

        public HourlyPrecipitation(ZonedDateTime zonedStartTime, ZonedDateTime zonedEndTime,
            Integer precipitationValue) {

            this.zonedStartTime = zonedStartTime;
            this.zonedEndTime = zonedEndTime;
            this.precipitationValue = precipitationValue;
            this.twelveHourStartTime = to12HrFrom24Hr(zonedStartTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX")));
            this.twelveHourEndTime = to12HrFrom24Hr(zonedEndTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX")));
        }

        public ZonedDateTime getZonedStartTime() {
            return zonedStartTime;
        }

        public ZonedDateTime getZonedEndTime() {
            return zonedEndTime;
        }

        public String getTwelveHourStartTime() {
            return twelveHourStartTime;
        }

        public String getTwelveHourEndTime() {
            return twelveHourEndTime;
        }

        public Integer getPrecipitationValue() {
            return precipitationValue;
        }
    }

    @JsonPropertyOrder({ "totalPeriods", "precipitationPeriods", "errMsg", "shortCode" })
    public static class GetPrecipitationTrendsResponse {
        private final List<HourlyPrecipitation> precipitationPeriods;
        private final Integer totalPeriods;
        private final String errMsg;
        private final ShortCode shortCode;

        public GetPrecipitationTrendsResponse(UserSettings userSettings, List<NWSHourlyPeriod> hourlyPeriods) {
            this.precipitationPeriods = new ArrayList<>();
            this.errMsg = null;
            this.shortCode = ShortCode.SUCCESS;
            this.totalPeriods = hourlyPeriods.size();

            hourlyPeriods.forEach(p -> {
                p.setZonedStartTime(userSettings.getTimeZone());
                p.setZonedEndTime(userSettings.getTimeZone());

                this.precipitationPeriods.add(
                    new HourlyPrecipitation(
                        p.zonedStartTime,
                        p.zonedEndTime,
                        p.getProbabilityOfPrecipitation().value()
                    )
                );
            });
        }

        public GetPrecipitationTrendsResponse(String errMsg, ShortCode shortCode) {
            this.precipitationPeriods = null;
            this.totalPeriods = null;
            this.errMsg = errMsg;
            this.shortCode = shortCode;
        }

        public List<HourlyPrecipitation> getPrecipitationPeriods() {
            return precipitationPeriods;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public ShortCode getShortCode() {
            return shortCode;
        }

        public Integer getTotalPeriods() {
            return totalPeriods;
        }
    }

    public GetPrecipitationTrendsResponse getPrecipitationTrends(UserSettings userSettings, TrendType trendType) {

        String forecastAPI = getForecastAPI(userSettings.getLat(), userSettings.getLon());
        String hourlyAPI = forecastAPI + "/hourly";

        NWSHourlyResponse response = this.nwsRestController
            .get()
            .uri(hourlyAPI)
            .retrieve()
            .body(NWSHourlyResponse.class);

        if (response == null) {
            return new GetPrecipitationTrendsResponse("NWS Hourly Forecast Response Was Null", SYSTEM_EXCEPTION);
        }
        List<NWSHourlyPeriod> hourlyPeriods = response.properties.periods;

        ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.now(),
            ZoneId.of(userSettings.getTimeZone()));
        ZonedDateTime startDay = ZonedDateTime.ofInstant(Instant.now(),
            ZoneId.of(userSettings.getTimeZone()));
        ZonedDateTime startTime = startDay.toLocalDate().atStartOfDay(startDay.getZone());
        List<NWSHourlyPeriod> filteredPeriods = new ArrayList<>();
        List<NWSHourlyPeriod> timeFiltered = new ArrayList<>();

        switch (trendType) {
            case ONE_DAY:
                endTime = startTime.plusDays(1).minusNanos(1);
                filteredPeriods = hourlyPeriods.stream()
                    .filter(p -> p.startTime.isAfter(startTime.toInstant().minusSeconds(1))
                        && p.endTime.isBefore(startTime.plusDays(1)
                        .minusNanos(1).toInstant().plusSeconds(1)))
                    .toList();
                break;

            case TWO_DAY:
                endTime = startTime.plusDays(2).minusNanos(1);
                timeFiltered = hourlyPeriods.stream()
                    .filter(p -> p.startTime.isAfter(startTime.toInstant().minusSeconds(1))
                    && p.endTime.isBefore(startTime.plusDays(2)
                        .minusNanos(1).toInstant().plusSeconds(1)))
                    .toList();

                // Skip every other entry to still wind up with 18 entries
                for (int i = 0; i < timeFiltered.size(); i += 2) {
                    filteredPeriods.add(timeFiltered.get(i));
                }
                break;

            case THREE_DAY:
                endTime = startTime.plusDays(3).minusNanos(1);
                timeFiltered = hourlyPeriods.stream()
                    .filter(p -> p.startTime.isAfter(startTime.toInstant().minusSeconds(1))
                        && p.endTime.isBefore(startTime.plusDays(3)
                        .minusNanos(1).toInstant().plusSeconds(1)))
                    .toList();

                // Skip to every third entry to wind up with 18 total entries
                for (int i = 0; i < timeFiltered.size(); i += 3) {
                    filteredPeriods.add(timeFiltered.get(i));
                }
                break;

            case FIVE_DAY:
                endTime = startTime.plusDays(5).minusNanos(1);
                timeFiltered = hourlyPeriods.stream()
                    .filter(p -> p.startTime.isAfter(startTime.toInstant().minusSeconds(1))
                        && p.endTime.isBefore(startTime.plusDays(5)
                        .minusNanos(1).toInstant().plusSeconds(1)))
                    .toList();

                // Skip to every fifth entry to wind up with 18 total entries
                for (int i = 0; i < timeFiltered.size(); i += 5) {
                    filteredPeriods.add(timeFiltered.get(i));
                }
                break;

            case SEVEN_DAY:
                endTime = startTime.plusDays(7).minusNanos(1);
                timeFiltered = hourlyPeriods.stream()
                    .filter(p -> p.startTime.isAfter(startTime.toInstant().minusSeconds(1))
                        && p.endTime.isBefore(startTime.plusDays(7)
                        .minusNanos(1).toInstant().plusSeconds(1)))
                    .toList();

                // Skip to every seventh entry to wind up with 18 total entries
                for (int i = 0; i < timeFiltered.size(); i += 7) {
                    filteredPeriods.add(timeFiltered.get(i));
                }
                break;
        }

        ZonedDateTime finalEndTime = endTime;
        return new GetPrecipitationTrendsResponse(userSettings, filteredPeriods);
    }
}
