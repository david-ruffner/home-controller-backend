package com.davidruffner.homecontrollerbackend.entities;

import java.util.List;

import static com.davidruffner.homecontrollerbackend.utils.Constants.DEFAULT_FAVORITE_COLORS_LENGTH;
import static com.davidruffner.homecontrollerbackend.utils.Constants.DEFAULT_FAV_COLOR;

public class LightBulb {
    private final String deviceId;
    private final String lightId;

    private String name;
    private RGB color;
    private double brightness;
    private boolean lightStatus;
    private List<FavoriteColor> favoriteColors;

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

    public List<FavoriteColor> getFavoriteColors() {
        return favoriteColors;
    }

    public void setFavoriteColors(List<FavoriteColor> favoriteColors) {
        // If there are less than the default amount of favorite colors, add some padding colors.
        int leftoverFavoriteColors = DEFAULT_FAVORITE_COLORS_LENGTH - favoriteColors.size();
        for (int i = 0; i < leftoverFavoriteColors; i++) {
            FavoriteColor defaultFavColor = new FavoriteColor();
            defaultFavColor.setColorFromRGB(DEFAULT_FAV_COLOR);
            favoriteColors.add(defaultFavColor);
        }

        this.favoriteColors = favoriteColors;
    }
}
