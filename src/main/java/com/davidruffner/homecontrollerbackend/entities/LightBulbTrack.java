package com.davidruffner.homecontrollerbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "light_bulbs")
public class LightBulbTrack {

    @Id
    @Column(name = "light_bulb_id", nullable = false)
    private String lightBulbId;

    @Column(name = "light_id", nullable = false)
    private String lightId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "brightness", nullable = false)
    private Double brightness;

    @Column(name = "red", nullable = false)
    private Double red;

    @Column(name = "green", nullable = false)
    private Double green;

    @Column(name = "blue", nullable = false)
    private Double blue;

    @Column(name = "isOn", nullable = false)
    private Integer isOn;

    @Column(name = "name", nullable = false)
    private String name;

    public LightBulbTrack() {
        this.lightBulbId = UUID.randomUUID().toString();
    }

    public String getLightBulbId() {
        return lightBulbId;
    }

    public String getLightId() {
        return lightId;
    }

    public void setLightId(String lightId) {
        this.lightId = lightId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getBrightness() {
        return brightness;
    }

    public void setBrightness(Double brightness) {
        this.brightness = brightness;
    }

    public Double getRed() {
        return red;
    }

    public void setRed(Double red) {
        this.red = red;
    }

    public Double getGreen() {
        return green;
    }

    public void setGreen(Double green) {
        this.green = green;
    }

    public Double getBlue() {
        return blue;
    }

    public void setBlue(Double blue) {
        this.blue = blue;
    }

    public Boolean getIsOn() {
        return isOn == 1;
    }

    public void setIsOn(Boolean isOn) {
        if (isOn) {
            this.isOn = 1;
        } else {
            this.isOn = 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRGBAString() {
        StringBuilder builder = new StringBuilder("rgba(");
        builder.append(this.getRed())
            .append(", ")
            .append(this.getGreen())
            .append(", ")
            .append(this.getBlue())
            .append(", ")
            .append(this.getBrightness() / 100)
            .append(")");

        return builder.toString();
    }

    public String getSliderRGBAString() {
        StringBuilder builder = new StringBuilder("rgba(");
        builder.append(this.getRed())
            .append(", ")
            .append(this.getGreen())
            .append(", ")
            .append(this.getBlue())
            .append(", 1)");

        return builder.toString();
    }
}
