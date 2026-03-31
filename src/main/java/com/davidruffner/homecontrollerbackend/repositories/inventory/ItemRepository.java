package com.davidruffner.homecontrollerbackend.repositories.inventory;

import com.davidruffner.homecontrollerbackend.entities.inventory.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, String> {

    @Query("""
        select item from Item item
            join fetch item.category
            join fetch item.room
            join fetch item.itemContainer
        where item.itemId = :itemId
    """)
    Optional<Item> fetchById(@Param("itemId") String itemId);

    @Query("""
        select item from Item item
            join fetch item.category
            join fetch item.room
            join fetch item.itemContainer
        where item.itemContainer.containerId = :containerId
    """)
    List<Item> getItemsByContainerId(@Param("containerId") String containerId);
}
