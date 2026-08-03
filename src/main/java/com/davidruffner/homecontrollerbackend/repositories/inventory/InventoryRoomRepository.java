package com.davidruffner.homecontrollerbackend.repositories.inventory;

import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRoomRepository extends JpaRepository<InventoryRoom, String> {

    @Query("""
        select ir from InventoryRoom ir
    """)
    List<InventoryRoom> getAllRooms();
}
