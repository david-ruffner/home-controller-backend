package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.entities.RGB;
import com.davidruffner.homecontrollerbackend.repositories.FavoriteColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesService {

    @Autowired
    FavoriteColorRepository favoriteColorRepo;

//    public List<RGB> getFavoriteColorsForSingleLight() {
//
//    }
}
