package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.config.FavoriteColorsConfig;
import com.davidruffner.homecontrollerbackend.controllers.FavoritesController;
import com.davidruffner.homecontrollerbackend.controllers.FavoritesController.AddFavoriteRequestDto;
import com.davidruffner.homecontrollerbackend.entities.FavoriteColor;
import com.davidruffner.homecontrollerbackend.entities.RGB;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.repositories.FavoriteColorRepository;
import com.davidruffner.homecontrollerbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavoritesService {

    @Autowired
    FavoriteColorRepository favoriteColorRepo;

    @Autowired
    FavoriteColorsConfig favoriteColorsConfig;

    public List<FavoriteColor> getFavoriteColorsForRoom(String roomId) {
        return this.favoriteColorRepo.getFavoriteColorsForRoom(roomId);
    }

    public FavoriteColor addFavoriteColorForRoom(AddFavoriteRequestDto favoriteDto) throws ControllerException {
        try {
            List<FavoriteColor> favoriteColors = getFavoriteColorsForRoom(favoriteDto.roomId());

            if (favoriteColors.size() >= favoriteColorsConfig.getMaxFavoriteColors()) {
                // Get the color that's been there the longest and delete it
                FavoriteColor fc = favoriteColorRepo.getEarliestFavoriteColorOfRoom(favoriteDto.roomId());
                favoriteColorRepo.delete(fc);
            }

            // Add the new favorite color
            FavoriteColor newFc = new FavoriteColor();
            newFc.setRoomId(favoriteDto.roomId());

            RGB rgb = new RGB(favoriteDto.rgb().red(), favoriteDto.rgb().green(), favoriteDto.rgb().blue(),
                    favoriteDto.rgb().alpha());
            newFc.setColorFromRGB(rgb);

            newFc.setFavoriteColorId(Utils.generateRandomB64Str(64));
            newFc.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));

            favoriteColorRepo.save(newFc);

            return newFc;
        } catch (Exception ex) {
            throw new ControllerException(ex.getMessage(), ResponseCode.SYSTEM_EXCEPTION);
        }
    }
}
