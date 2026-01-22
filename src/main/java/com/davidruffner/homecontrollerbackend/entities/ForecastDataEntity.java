package com.davidruffner.homecontrollerbackend.entities;

import com.davidruffner.homecontrollerbackend.dtoConverters.ForecastDataConverter;
import com.davidruffner.homecontrollerbackend.dtos.ForecastData;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "forecast_data")
public class ForecastDataEntity {
    @Id
    @Column(name = "forecast_data_id", nullable = false)
    private String forecastDataId;

    @Column(name = "type", nullable = false)
    private String type;

    @Convert(converter = ForecastDataConverter.class)
    @Column(name = "forecast_data", nullable = false)
    private List<ForecastData> forecastData;

    @Column(name = "generated_time", nullable = false)
    private LocalDateTime generatedTime;

    public ForecastDataEntity() {
        this.forecastDataId = UUID.randomUUID().toString();
    }

    public String getForecastDataId() {
        return forecastDataId;
    }

    public void setForecastDataId(String forecastDataId) {
        this.forecastDataId = forecastDataId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ForecastData> getForecastData() {
        return forecastData;
    }

    public void setForecastData(List<ForecastData> forecastData) {
        this.forecastData = forecastData;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }
}
