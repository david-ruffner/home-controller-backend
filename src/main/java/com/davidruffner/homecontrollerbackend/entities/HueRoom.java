package com.davidruffner.homecontrollerbackend.entities;

import com.davidruffner.homecontrollerbackend.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.davidruffner.homecontrollerbackend.utils.Constants.*;

public class HueRoom {
    private String name;
    private String groupToggleId;
    private List<LightBulb> lightBulbs = new ArrayList<>();
    private List<FavoriteColor> favoriteColors;

    public HueRoom(String name) {
        this.name = name;
    }

    public HueRoom(String name, String groupToggleId) {
        this.name = name;
        this.groupToggleId = groupToggleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupToggleId() {
        return groupToggleId;
    }

    public void setGroupToggleId(String groupToggleId) {
        this.groupToggleId = groupToggleId;
    }

    public List<LightBulb> getLightBulbs() {
        return lightBulbs;
    }

    public void setLightBulbs(List<LightBulb> lightBulbs) {
        this.lightBulbs = lightBulbs;
    }

    public void addLightBulb(LightBulb lightBulb) {
        this.lightBulbs.add(lightBulb);
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
