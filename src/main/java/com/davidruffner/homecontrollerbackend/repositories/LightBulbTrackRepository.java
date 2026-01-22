package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.entities.LightBulbTrack;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LightBulbTrackRepository extends JpaRepository<LightBulbTrack, String> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update LightBulbTrack lb
            set lb.isOn = :isOn
        where lb.lightId = :id
        or lb.deviceId = :id
    """)
    void updateIsOn(Integer isOn, String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update LightBulbTrack lb
            set lb.brightness = :brightness
        where lb.lightId = :id
        or lb.deviceId = :id
    """)
    void updateBrightness(Double brightness, String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update LightBulbTrack lb
            set lb.red = :red,
                lb.green = :green,
                lb.blue = :blue
        where lb.lightId = :id
        or lb.deviceId = :id
    """)
    void updateColor(Double red, Double green, Double blue, String id);

    @Query("""
        select lb from LightBulbTrack lb
        where lb.deviceId = :id
        or lb.lightId = :id
    """)
    Optional<LightBulbTrack> getCachedBulb(@Param("id") String id);
}
