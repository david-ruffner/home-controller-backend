package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.entities.Room;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("""
        select room from Room room
        where room.roomId = :roomId
    """)
    Optional<Room> fetchById(@Param("roomId") String roomId);

    @Query("""
        select room from Room room
    """)
    List<InventoryRoom> fetchAllRooms();
}
