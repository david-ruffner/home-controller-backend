package com.davidruffner.homecontrollerbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.favorite-colors")
public class FavoriteColorsConfig {

    private int maxFavoriteColors;

    public FavoriteColorsConfig(int maxFavoriteColors) {
        this.maxFavoriteColors = maxFavoriteColors;
    }

    public int getMaxFavoriteColors() {
        return maxFavoriteColors;
    }

    public void setMaxFavoriteColors(int maxFavoriteColors) {
        this.maxFavoriteColors = maxFavoriteColors;
    }
}
