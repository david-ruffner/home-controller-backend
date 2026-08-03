package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.entities.FavoriteColor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteColorRepository extends JpaRepository<FavoriteColor, String> {
    @Query("""
        select c from FavoriteColor c
        where c.roomId = :roomId
    """)
    public List<FavoriteColor> getFavoriteColorsForRoom(@Param("roomId") String roomId);

    @Modifying
    @Transactional
    @Query("""
        delete from FavoriteColor fc
        where fc.roomId = :roomId
            and fc.favoriteColorId = :favoriteColorId
    """)
    void deleteFavoriteColorForRoom(@Param("roomId") String roomId,
                                           @Param("favoriteColorId") String favoriteColorId);

    @Query("""
        select c from FavoriteColor c
        where c.roomId = :roomId
        order by c.timestamp asc
        limit 1
    """)
    public FavoriteColor getEarliestFavoriteColorOfRoom(@Param("roomId") String roomId);
}
