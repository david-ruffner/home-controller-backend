package com.davidruffner.homecontrollerbackend.repositories.inventory;

import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<InventoryCategory, String> {

    @Query("""
        select cat from InventoryCategory cat
        left join fetch cat.items
        where cat.categoryId = :categoryId
    """)
    Optional<InventoryCategory> fetchById(@Param("categoryId") String categoryId);
}
