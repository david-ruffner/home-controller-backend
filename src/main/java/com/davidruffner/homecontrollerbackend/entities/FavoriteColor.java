package com.davidruffner.homecontrollerbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "favorite_colors")
public class FavoriteColor {
    @Id
    @Column(name = "favorite_color_id", nullable = false)
    private String favoriteColorId;

    @Column(name = "light_id")
    private String lightId;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "color", nullable = false)
    private String color; // Stored as red,green,blue,alpha

    @Column(name = "control_device_id", nullable = false)
    private String controlDeviceId;

    @Column(name = "index_num", nullable = false)
    private Integer index;

    public FavoriteColor() {
        this.favoriteColorId = UUID.randomUUID().toString();
    }

    public String getFavoriteColorId() {
        return favoriteColorId;
    }

    public void setFavoriteColorId(String favoriteColorId) {
        this.favoriteColorId = favoriteColorId;
    }

    public String getLightId() {
        return lightId;
    }

    public void setLightId(String lightId) {
        this.lightId = lightId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getColor() {
        return color;
    }

    public RGB getColorAsRGB() {
        return new RGB(this.color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setColorFromRGB(RGB rgbColor) {
        this.color = rgbColor.toString();
    }

    public void setControlDeviceId(String controlDeviceId) {
        this.controlDeviceId = controlDeviceId;
    }

    public String getControlDeviceId() {
        return controlDeviceId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getRGBAsString() {
        StringBuilder builder = new StringBuilder("rgba(");
        builder.append(this.getColor());
        builder.append(")");

        return builder.toString();
    }
}
