package com.davidruffner.homecontrollerbackend.dtos;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetUserSettingsResponseDTO {
    private final String controlDeviceId;
    private final String address;
    private final String lat;
    private final String lon;
    private final String weatherApiKey;
    private final String hueApiKey;
    private final String todoistApiKey;
    private final String geoapifyKey;
    private final List<String> columnsToAdd = new ArrayList<>();
    private final String errMsg;

    public GetUserSettingsResponseDTO(String errMsg) {
        this.errMsg = errMsg;
        this.controlDeviceId = null;
        this.address = null;
        this.lat = null;
        this.lon = null;
        this.weatherApiKey = null;
        this.hueApiKey = null;
        this.todoistApiKey = null;
        this.geoapifyKey = null;
    }

    public GetUserSettingsResponseDTO(UserSettings userSettings) {
        this.errMsg = null;
        this.controlDeviceId = userSettings.getControlDeviceId();
        this.address = userSettings.getAddress();
        this.lat = userSettings.getLat();
        this.lon = userSettings.getLon();
        this.weatherApiKey = userSettings.getWeatherApiKey();
        this.hueApiKey = userSettings.getHueApiKey();
        this.todoistApiKey = userSettings.getTodoistApiKey();
        this.geoapifyKey = userSettings.getGeoapifyKey();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.getName().equals("errMsg")) {
                field.setAccessible(true);

                try {
                    Object val = field.get(this);

                    if (val == null) {
                        this.columnsToAdd.add(field.getName());
                    }
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public String getControlDeviceId() {
        return controlDeviceId;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getWeatherApiKey() {
        return weatherApiKey;
    }

    public String getHueApiKey() {
        return hueApiKey;
    }

    public String getTodoistApiKey() {
        return todoistApiKey;
    }

    public String getGeoapifyKey() {
        return geoapifyKey;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public List<String> getColumnsToAdd() {
        return columnsToAdd;
    }
}
