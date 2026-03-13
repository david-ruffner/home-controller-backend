package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.HueGroupResponseDto;
import com.davidruffner.homecontrollerbackend.HueGroupResponseDto.GetBulbsForGroupResponse;
import com.davidruffner.homecontrollerbackend.entities.*;
import com.davidruffner.homecontrollerbackend.entities.ModifyLightRequest.ModifyLightRequestDTO;
import com.davidruffner.homecontrollerbackend.repositories.FavoriteColorRepository;
import com.davidruffner.homecontrollerbackend.repositories.LightBulbTrackRepository;
import com.davidruffner.homecontrollerbackend.services.ColorConversionService;
import com.davidruffner.homecontrollerbackend.services.ColorConversionService.RgbToXyDto;
import com.davidruffner.homecontrollerbackend.services.LightsService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.davidruffner.homecontrollerbackend.utils.Utils.strNotEmpty;

@RestController
@RequestMapping("/lights")
public class LightsController {

    @Autowired
    LightsService lightsService;

    @Autowired
    ColorConversionService colorConversionService;

    @Autowired
    LightBulbTrackRepository lightBulbTrackRepo;

    @Autowired
    FavoriteColorRepository favoriteColorRepo;


    @GetMapping("/getRooms")
    public ResponseEntity<Map<String, HueRoom>> getRooms() {
        return ResponseEntity.ok(this.lightsService.getLightBulbsMappedByRoom());
    }

    /**
     * Retrieves room by ID or returns bad request if absent
     */
    @GetMapping("/getRoom/{roomId}")
    public ResponseEntity<HueRoom> getRoom(@PathVariable String roomId) {
        if (!this.lightsService.getLightBulbsMappedByRoom().containsKey(roomId)) {
            return ResponseEntity.badRequest()
                .build();
        }

        HueRoom hueRoom = this.lightsService.getLightBulbsMappedByRoom().get(roomId);
        List<FavoriteColor> favoriteColors = this.favoriteColorRepo.getColorsByGroupId(hueRoom.getGroupToggleId());
        hueRoom.setFavoriteColors(favoriteColors);

        return ResponseEntity.ok(hueRoom);
    }

    @GetMapping("/getBulbs")
    public ResponseEntity<List<LightBulb>> getBulbs() {
        return ResponseEntity.ok(this.lightsService.getAllLightBulbs());
    }

    public record ToggleGroupLightRequestDTO(
        String groupId,
        Boolean status
    ) {}

    public record ToggleGroupLightResponseDTO(
        Boolean status,
        Map<String, Boolean> affectedLights
    ) {}

    @PostMapping("/toggleGroupLighting")
    public ResponseEntity<LightsService.ToggleLightGroupResponseDTO> toggleGroupLighting(@RequestBody ToggleGroupLightRequestDTO body) {
        // Here, status refers to whether or not the command was successful.
        LightsService.ToggleLightGroupResponseDTO response = lightsService.toggleLightGroup(body.groupId(), body.status());

        return ResponseEntity.ok(response);
    }

    public record ToggleLightRequestDTO(
        String lightId,
        Boolean status
    ) {}

    public record ToggleLightResponseDTO(
        Boolean status
    ) {}

    @PostMapping("/toggleLight")
    public ResponseEntity<ToggleLightResponseDTO> toggleLight(@RequestBody ToggleLightRequestDTO body) {
        boolean status = this.lightsService.toggleLight(body.lightId(), body.status());

        return new ResponseEntity<>(new ToggleLightResponseDTO(status), HttpStatus.OK);
    }

    @PostMapping("/modifyLight")
    public ResponseEntity<Boolean> modifyLight(@RequestBody ModifyLightRequestDTO body) throws Exception {
        ModifyLightRequest request = new ModifyLightRequest(body); // Validation in constructor

        if (request.getDimPercent().isPresent()) {
            // Dim the lights
            Double dimPercent = request.getDimPercent().get();

            if (request.getLightId().isPresent()) {
                // Only one light
                this.lightsService.modifyLightBrightness(request.getLightId().get(), dimPercent);
            } else {
                // A light group
                this.lightsService.modifyGroupBrightness(request.getGroupId().get(), dimPercent);
            }
        }

        if (request.getRgb().isPresent()) {
            // Modify the color
            RGB rgb = request.getRgb().get();
            RgbToXyDto xy = this.colorConversionService.rgbToXy(rgb);

            if (request.getLightId().isPresent()) {
                // Only one light
                this.lightsService.modifyLightColor(request.getLightId().get(), xy);
            } else {
                // Group of lights
                this.lightsService.modifyGroupColor(request.getGroupId().get(), xy, rgb);
            }
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/getCachedLightBulb/{id}")
    public ResponseEntity<Optional<LightBulbTrack>> getCachedLightBulb(@PathVariable String id) {
        return ResponseEntity.ok(this.lightBulbTrackRepo.getCachedBulb(id));
    }

    @GetMapping("/getBulbsForGroup/{groupId}")
    public ResponseEntity<GetBulbsForGroupResponse> getBulbsForGroup(@PathVariable String groupId) {
        ResponseEntity<GetBulbsForGroupResponse> response = this.lightsService.getBulbsForGroup(groupId);

        return response;
    }

    public record UpdateFavoriteColorsRequest(
        String controlDeviceId,
        String groupId,
        String lightId,
        String colorString,
        String favoriteColorId
    ) {}

    @PostMapping("/updateFavoriteColors")
    public ResponseEntity<Void> updateFavoriteColors(@RequestBody UpdateFavoriteColorsRequest body) {
        if (strNotEmpty(body.favoriteColorId())) {
            if (this.favoriteColorRepo.findById(body.favoriteColorId()).isPresent()) {
                this.favoriteColorRepo.updateFavoriteColorByFavoriteColorId(body.colorString(),
                    body.favoriteColorId());
            } else {
                FavoriteColor newFavColor = new FavoriteColor(body.favoriteColorId());
                newFavColor.setControlDeviceId(body.controlDeviceId());
                newFavColor.setGroupId(body.groupId());
                newFavColor.setLightId(body.lightId());
                newFavColor.setColorFromRGB(new RGB(body.colorString()));
                newFavColor.setIndex(1);

                this.favoriteColorRepo.save(newFavColor);
            }
        }

        return ResponseEntity.ok().build();
    }
}
