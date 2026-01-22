package com.davidruffner.homecontrollerbackend.entities;

import java.util.Objects;
import java.util.Optional;

public class RGB {
    private final Double red;
    private final Double green;
    private final Double blue;
    private final Optional<Double> alpha;

    public RGB(Double red, Double green, Double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = Optional.empty();
    }

    public RGB(Double red, Double green, Double blue, Double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = Optional.ofNullable(alpha);
    }

    public RGB(String rgbString) {
        String[] rgbVals = rgbString.split(",");

        this.red = Double.valueOf(rgbVals[0]);
        this.green = Double.valueOf(rgbVals[1]);
        this.blue = Double.valueOf(rgbVals[2]);

        if (rgbVals.length == 4) {
            this.alpha = Optional.of(Double.valueOf(rgbVals[3]));
        } else {
            this.alpha = Optional.empty();
        }
    }

    public Double getRed() {
        return red;
    }

    public Double getGreen() {
        return green;
    }

    public Double getBlue() {
        return blue;
    }

    public Optional<Double> getAlpha() {
        return alpha;
    }

    @Override
    public String toString() {
        // Red,Green,Blue,Alpha?
        StringBuilder builder = new StringBuilder(this.red.toString());
        builder.append(",");
        builder.append(this.green.toString());
        builder.append(",");
        builder.append(this.blue.toString());

        if (this.alpha.isPresent()) {
            builder.append(",");
            builder.append(this.alpha.get());
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RGB)) {
            return false;
        }
        RGB newRgb = (RGB) obj;

        if (this.alpha.isPresent()) {
            return (Objects.equals(this.red, newRgb.getRed()) && Objects.equals(this.green, newRgb.getGreen())
            && Objects.equals(this.blue, newRgb.getBlue()) && Objects.equals(this.alpha.get(), newRgb.getAlpha().get()));
        } else {
            return (Objects.equals(this.red, newRgb.getRed()) && Objects.equals(this.green, newRgb.getGreen())
                && Objects.equals(this.blue, newRgb.getBlue()));
        }
    }
}
