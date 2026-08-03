package com.davidruffner.homecontrollerbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "favorite_colors")
public class FavoriteColor {
    @Id
    @Column(name = "favorite_color_id", nullable = false)
    private String favoriteColorId;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "color", nullable = false)
    private String color; // Stored as red,green,blue,alpha

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    public FavoriteColor() {
        this.favoriteColorId = UUID.randomUUID().toString();
    }

    public FavoriteColor(String favoriteColorId) {
        this.favoriteColorId = favoriteColorId;
    }

    public String getFavoriteColorId() {
        return favoriteColorId;
    }

    public void setFavoriteColorId(String favoriteColorId) {
        this.favoriteColorId = favoriteColorId;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getRGBAsString() {
        StringBuilder builder = new StringBuilder("rgba(");
        builder.append(this.getColor());
        builder.append(")");

        return builder.toString();
    }
}
