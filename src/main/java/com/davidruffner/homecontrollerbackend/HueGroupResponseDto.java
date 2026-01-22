package com.davidruffner.homecontrollerbackend;

import com.davidruffner.homecontrollerbackend.entities.LightBulbTrack;
import com.davidruffner.homecontrollerbackend.services.LightsService;

import java.util.List;

public class HueGroupResponseDto {
    public record HueGroupDimmingResponse(
        Double brightness
    ) {}

    public record HueGroupIsOnResponse(
        Boolean on
    ) {}

    public record HueGroupDataOwnerResponse(
        String rid,
        String rtype
    ) {}

    public record HueGroupDataResponse(
        HueGroupDataOwnerResponse owner,
        HueGroupIsOnResponse on,
        HueGroupDimmingResponse dimming
    ) {}

    public record HueGroupResponse(
        List<Object> errors,
        List<HueGroupDataResponse> data
    ) {}

    public record GetBulbsForGroupResponse(
        String groupedLightId,
        String roomName,
        Boolean isMultiColor,
        String colorGradient,
        String textColor,
        Boolean isGroupOn,
        Double brightness,
        List<LightBulbTrack> lightBulbs
    ) {}
}
