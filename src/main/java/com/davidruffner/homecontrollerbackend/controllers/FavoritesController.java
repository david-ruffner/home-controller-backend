package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.entities.FavoriteColor;
import com.davidruffner.homecontrollerbackend.entities.RGB;
import com.davidruffner.homecontrollerbackend.repositories.FavoriteColorRepository;
import com.davidruffner.homecontrollerbackend.services.FavoritesService;
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

    @Autowired
    FavoritesService favoritesService;

    @GetMapping("/getFavorites/{roomId}")
    public ResponseEntity<List<FavoriteColor>> getFavorites(@PathVariable String roomId) throws Exception {
        return ResponseEntity.ok(favoriteColorRepo.getFavoriteColorsForRoom(roomId));
    }

    public record AddFavoriteRequestRGBDto (
        Double red,
        Double green,
        Double blue,
        Double alpha
    ) {}

    public record AddFavoriteRequestDto (
        String roomId,
        AddFavoriteRequestRGBDto rgb
    ) {}

    @PostMapping("/addFavorite")
    public ResponseEntity<FavoriteColor> addFavorite(@RequestBody AddFavoriteRequestDto body) {
        FavoriteColor fc = this.favoritesService.addFavoriteColorForRoom(body);

        return ResponseEntity.ok(fc);
    }

    public record RemoveFavoriteRequestDTO(
        String favoriteColorId,
        String roomId
    ) {}

    public record RemoveFavoriteResponseDTO(
        Boolean status,
        String message
    ) {}

    @PostMapping("/removeFavorite")
    public ResponseEntity<RemoveFavoriteResponseDTO> removeFavorite(@RequestBody RemoveFavoriteRequestDTO body) {
        try {
            this.favoriteColorRepo.deleteFavoriteColorForRoom(body.roomId(), body.favoriteColorId());

            return ResponseEntity.ok(new RemoveFavoriteResponseDTO(true, ""));
        } catch (Exception ex) {
            // TODO: Properly log this

            return ResponseEntity.status(500)
                    .body(new RemoveFavoriteResponseDTO(false, ex.getMessage()));
        }
    }
}
