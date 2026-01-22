package com.davidruffner.homecontrollerbackend.entities;

public class LightBulb {
    private final String deviceId;
    private final String lightId;

    private String name;
    private RGB color;
    private double brightness;
    private boolean lightStatus;

    public LightBulb(String deviceId, String lightId) {
        this.deviceId = deviceId;
        this.lightId = lightId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLightId() {
        return lightId;
    }

    public String getName() {
        return name;
    }

    public double getBrightness() {
        return brightness;
    }

    public RGB getColor() {
        return color;
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public boolean getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(boolean lightStatus) {
        this.lightStatus = lightStatus;
    }
}
