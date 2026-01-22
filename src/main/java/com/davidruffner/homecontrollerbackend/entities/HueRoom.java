package com.davidruffner.homecontrollerbackend.entities;

import java.util.ArrayList;
import java.util.List;

public class HueRoom {
    private String name;
    private String groupToggleId;
    private List<LightBulb> lightBulbs = new ArrayList<>();

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
}
