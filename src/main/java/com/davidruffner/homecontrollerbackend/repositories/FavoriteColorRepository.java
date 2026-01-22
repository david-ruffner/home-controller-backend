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
        where c.groupId = :groupId
    """)
    public Optional<FavoriteColor> getColorByGroupId(@Param("groupId") String groupId);

    @Query("""
        select c from FavoriteColor c
        where c.lightId = :lightId
    """)
    public Optional<FavoriteColor> getColorByLightId(@Param("lightId") String lightId);

    @Query("""
        select c from FavoriteColor c
        where c.controlDeviceId = :controlDeviceId
            and c.lightId is not null
            and c.groupId is null
    """)
    public List<FavoriteColor> getFavoriteColorsForSingleLight(@Param("controlDeviceId") String controlDeviceId);

    @Query("""
        select c from FavoriteColor c
        where c.controlDeviceId = :controlDeviceId
            and c.lightId is null
            and c.groupId is not null
    """)
    public List<FavoriteColor> getFavoriteColorForLightGroup(@Param("controlDeviceId") String controlDeviceId);

    @Modifying
    @Transactional
    @Query("""
        delete from FavoriteColor c
        where c.index = :indexNum
    """)
    public void deleteByIndexNum(@Param("indexNum") Integer indexNum);
}
