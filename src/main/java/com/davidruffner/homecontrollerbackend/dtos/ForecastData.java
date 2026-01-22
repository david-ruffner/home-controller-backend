package com.davidruffner.homecontrollerbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastData {

    private OffsetDateTime zonedStartTime;
    private OffsetDateTime zonedEndTime;
    private Double temperature;
    private ForecastDataValueWrapper relativeHumidity;
    private ForecastDataValueWrapper probabilityOfPrecipitation;
    private String windSpeed;
    private String windDirection;
    private String shortForecast;
    private Integer feelsLikeTemp;
    private Integer temperatureInt;
    private String temperatureStr;
    private String windStr;

    public OffsetDateTime getZonedStartTime() {
        return zonedStartTime;
    }

    public void setZonedStartTime(OffsetDateTime zonedStartTime) {
        this.zonedStartTime = zonedStartTime;
    }

    public OffsetDateTime getZonedEndTime() {
        return zonedEndTime;
    }

    public void setZonedEndTime(OffsetDateTime zonedEndTime) {
        this.zonedEndTime = zonedEndTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public ForecastDataValueWrapper getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(ForecastDataValueWrapper relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public ForecastDataValueWrapper getProbabilityOfPrecipitation() {
        return probabilityOfPrecipitation;
    }

    public void setProbabilityOfPrecipitation(ForecastDataValueWrapper probabilityOfPrecipitation) {
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getShortForecast() {
        return shortForecast;
    }

    public void setShortForecast(String shortForecast) {
        this.shortForecast = shortForecast;
    }

    public Integer getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    public void setFeelsLikeTemp(Integer feelsLikeTemp) {
        this.feelsLikeTemp = feelsLikeTemp;
    }

    public Integer getTemperatureInt() {
        return temperatureInt;
    }

    public void setTemperatureInt(Integer temperatureInt) {
        this.temperatureInt = temperatureInt;
    }

    public String getTemperatureStr() {
        return temperatureStr;
    }

    public void setTemperatureStr(String temperatureStr) {
        this.temperatureStr = temperatureStr;
    }

    public String getWindStr() {
        return windStr;
    }

    public void setWindStr(String windStr) {
        this.windStr = windStr;
    }
}
