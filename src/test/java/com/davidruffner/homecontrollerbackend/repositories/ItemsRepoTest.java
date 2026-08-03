package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryCategory;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryItem;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryRoom;
import com.davidruffner.homecontrollerbackend.repositories.inventory.CategoryRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.InventoryRoomRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemsRepoTest {

    @Autowired
    ItemRepository itemRepo;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    InventoryRoomRepository roomRepo;

    @Test
    public void testInventoryEntities() {
        InventoryCategory cat = new InventoryCategory();
        cat.setCategoryName("Medicine");
        String categoryId = cat.getCategoryId();

        InventoryRoom room = new InventoryRoom();
        room.setRoomName("Kitchen");
        String roomId = room.getRoomId();

        InventoryItem item = new InventoryItem();
        item.setItemName("My Item");
        item.setItemDescription("Description");
        item.setUpc("FJJFD8AJFDAJF");
        item.setQuantity(3);
        item.setInventoryCategory(cat);
        item.setInventoryRoom(room);
        String item1Id = item.getItemId();

        InventoryItem item2 = new InventoryItem();
        item2.setItemName("2nd Item");
        item2.setItemDescription("Description");
        item2.setUpc("FJKFDAJ8FDJAF");
        item2.setQuantity(6);
        item2.setInventoryCategory(cat);
        item2.setInventoryRoom(room);
        String item2Id = item2.getItemId();

        this.categoryRepo.save(cat);
        this.roomRepo.save(room);
        this.itemRepo.save(item);
        this.itemRepo.save(item2);

        InventoryItem actualItem1 = this.itemRepo.fetchById(item1Id).get();
        InventoryItem actualItem2 = this.itemRepo.fetchById(item2Id).get();
        InventoryCategory actualCategory = this.categoryRepo.fetchById(categoryId).get();
        InventoryRoom actualRoom = this.roomRepo.findById(roomId).get();

        assertEquals(categoryId, actualItem1.getInventoryCategory().getCategoryId());
        assertEquals(roomId, actualItem2.getInventoryRoom().getRoomId());

        assertFalse(actualCategory.getItems()
            .stream()
            .filter(i -> i.getItemId().equals(item1Id))
            .toList().isEmpty());
        assertFalse(actualRoom.getItems()
            .stream()
            .filter(r -> r.getItemId().equals(item2Id))
            .toList().isEmpty());
    }
}
