package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.controllers.UserSettingsController;
import com.davidruffner.homecontrollerbackend.controllers.UserSettingsController.UserAndName;
import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, String> {
    @Query("""
        select us from UserSettings us
        where us.controlDeviceId = :controlDeviceId
    """)
    Optional<UserSettings> getUserSettingsByControlDeviceId(
        @Param("controlDeviceId") String controlDeviceId);

    @Query("""
        select us from UserSettings us
        where us.pinNumber = :pinHash
            and us.username = :username
    """)
    Optional<UserSettings> getUserSettingsByPinHashAndUsername(
        @Param("pinHash") String pinHash, @Param("username") String username);

//    record UserAndName(
//        String username,
//        String name
//    ) {}

    @Query("""
        select us.username, us.name from UserSettings us
    """)
    public List<UserAndName> getAllUsers();
}
