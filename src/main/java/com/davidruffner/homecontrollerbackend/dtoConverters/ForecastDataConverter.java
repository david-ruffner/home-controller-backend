package com.davidruffner.homecontrollerbackend.dtoConverters;

import com.davidruffner.homecontrollerbackend.dtos.ForecastData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter
public class ForecastDataConverter implements AttributeConverter<ForecastData, String> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(ForecastData forecastData) {
        if (forecastData == null) return null;

        try {
            return mapper.writeValueAsString(forecastData);
        } catch (Exception ex) {
            throw new IllegalArgumentException("failed to serialize ForecastData object");
        }
    }

    @Override
    public ForecastData convertToEntityAttribute(String s) {
        if (s == null || s.isBlank()) return null;

        try {
            return mapper.readValue(s, ForecastData.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to deserialize ForecastData object");
        }
    }
}
