package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.entities.FavoriteColor;
import com.davidruffner.homecontrollerbackend.entities.RGB;
import com.davidruffner.homecontrollerbackend.repositories.FavoriteColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    FavoriteColorRepository favoriteColorRepo;

    public record GetFavoritesRequestDto(
        String lightId,
        String groupId,
        String controlDeviceId
    ) {}

    public record GetFavoritesResponseDto(
        List<FavoriteColor> colors
    ) {}

    @PostMapping("/getFavorites")
    public ResponseEntity<List<FavoriteColor>> getFavorites(@RequestBody GetFavoritesRequestDto body) throws Exception {

        if (body.lightId() != null) {
            return ResponseEntity.ok(favoriteColorRepo.getFavoriteColorsForSingleLight(body.controlDeviceId));
        } else if (body.groupId != null) {
            return ResponseEntity.ok(favoriteColorRepo.getFavoriteColorForLightGroup(body.controlDeviceId));
        } else {
            throw new Exception("Neither lightId, or groupId were set!");
        }
    }

    public record AddFavoriteRequestRGBDto (
        Double red,
        Double green,
        Double blue,
        Double alpha
    ) {}

    public record AddFavoriteRequestDto (
        String lightId,
        String groupId,
        String controlDeviceId,
        Integer index,
        AddFavoriteRequestRGBDto rgb
    ) {}

    @PostMapping("/addFavorite")
    public ResponseEntity<FavoriteColor> addFavorite(@RequestBody AddFavoriteRequestDto body) {
        FavoriteColor favoriteColor = new FavoriteColor();
        favoriteColor.setLightId(body.lightId);
        favoriteColor.setGroupId(body.groupId);
        favoriteColor.setControlDeviceId(body.controlDeviceId);
        favoriteColor.setIndex(body.index);

        RGB rgb = new RGB(body.rgb.red, body.rgb.green, body.rgb.blue, body.rgb.alpha);
        favoriteColor.setColorFromRGB(rgb);

        // Remove existing colors with index num
        this.favoriteColorRepo.deleteByIndexNum(body.index);
        this.favoriteColorRepo.save(favoriteColor);

        return ResponseEntity.ok(favoriteColor);
    }
}
