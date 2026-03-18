package com.davidruffner.homecontrollerbackend.repositories.inventory;

import com.davidruffner.homecontrollerbackend.entities.inventory.Category;
import com.davidruffner.homecontrollerbackend.entities.inventory.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("""
        select room from Room room
        join fetch room.items
        where room.roomId = :roomId
    """)
    Optional<Room> fetchById(@Param("roomId") String roomId);

    @Query("""
        select room from Room room
    """)
    List<Room> fetchAllRooms();
}
