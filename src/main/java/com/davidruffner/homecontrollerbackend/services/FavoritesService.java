package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.entities.FavoriteColor;
import com.davidruffner.homecontrollerbackend.entities.RGB;
import com.davidruffner.homecontrollerbackend.repositories.FavoriteColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoritesService {

    @Autowired
    FavoriteColorRepository favoriteColorRepo;

    public List<FavoriteColor> getFavoriteColorsForSingleLight(String lightId) {
        return this.favoriteColorRepo.getColorsByLightId(lightId);
    }

    public List<FavoriteColor> getFavoriteColorsForLightGroup(String groupId) {
        return this.favoriteColorRepo.getColorsByGroupId(groupId);
    }
}
