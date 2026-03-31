package com.davidruffner.homecontrollerbackend.repositories.inventory;

import com.davidruffner.homecontrollerbackend.entities.inventory.ItemContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemContainerRepository extends JpaRepository<ItemContainer, String> {

    @Query("""
    select ic
    from ItemContainer ic
    join ic.item i
    where i.room.roomId = :roomId
""")
    List<ItemContainer> getContainersByRoomId(@Param("roomId") String roomId);
}
